# Mail Importer for Gmail: Design Guide

## Overview

The **Mail Importer** imports mail into Gmail from various sources using the
Gmail API v1. Currently, Mozilla Thunderbird is the only supported source, but
it should be relatively easy to extend support to any other local, mbox-based
source. (And, in fact, it may just work out of the box.)

The Mail Importer tries to be efficient by not uploading the message content
for messages that already exist in Gmail. This makes it easy for the import
process to be restarted if there is an error.

__Mail Importer__ requires Java 8 or later.

## General Design and Assumptions

Mail messages are imported from a _source_ represented by an object that
implements `LocalStorage`. The primary purpose of this interface is to return
`LocalMessage` objects that should be imported. Mail Importer assumes that the
source will not change while it is running.

`LocalMessage` encapsulates the information necessary to import a message using
the Gmail API. This includes the RFC 822 message ID, the _folder_ in which the
message is present, whether it is starred or read, and the raw message content.

The `Importer` class iterates through the messages returned by the source in
batches. For each batch, it queries the Gmail API for all of the RFC 822 message
IDs. For each message that is missing, it uploads the raw message data. Then,
for each message, it checks the Gmail labels that are on the message and
adjusts the labels if necessary.

The Mail Importer assumes that all messages with the same RFC 822 message ID in
the local store _are the same message_. This is very important for several
reasons, including:

* not requiring every message to be re-uploaded if an import is interrupted;
* ensuring that messages present in more than one local folder are labelled
  correctly in Gmail.

However, care has also been taken to not assume that there won't be multiple
copies of the same message in Gmail. (This can happen if there is a mis-behaved
mail client that actually sends different messages with the same message ID.)

When something goes wrong during an import, the Mail Importer invokes an _error
strategy_ to deal with the error. Several error strategies exist: failing the
import immediately, ignoring the error, and copying the failing message to a
different local folder so that it can be retried later.

## Detailed Design

### Reading Mailboxes

The Mail Importer uses [Mstor](http://mstor.sourceforge.net/) as a **JavaMail**
implementation to read local Thunderbird mailboxes. Mstor can also read regular
mbox files, but this has not been tested.

#### Thunderbird details

On top of Mstor, the Mail Importer's `ThunderbirdLocalMessage` class adds
support for interpreting the value of the `X-Mozilla-Status` header.
Specifically, it retrieves whether the user has read the message and whether
the message has been _flagged_. Flagged messages are converted to _starred_
messages in Gmail.

The class `ThunderbirdMailStorage` also filters the folders returned by the API
to only those that are real folders. For example, Thunderbird snapshots that
contain a `@` are ignored.

#### Dealing with `MessagingException`s

Almost every method in the JavaMail API throws a MessagingException if an error
occurs. This plethora of checked exceptions muddies the code, especially
because there is almost no way to recover from these exceptions. Here's an
example from the API:

````java
    /**
     * Return a URLName representing this folder.  The returned URLName
     * does <em>not</em> include the password used to access the store.
     *
     * @return	the URLName representing this folder
     * @see	URLName
     * @since	JavaMail 1.1
     */
    public URLName getURLName() throws MessagingException {...}
````

There is no clue as to why this method might throw a `MessagingException` nor
what those exceptions might mean. Digging through the code reveals that this is
because `Folder.getSeparator()` can throw a `MessagingException`.
[WAT?](http://mybroadband.co.za/vb/attachment.php?attachmentid=71940&d=1379691163)

In Mail Importer, all JavaMail objects are wrapped with objects that implement
the JavaMail API, but convert all `MessageException`s into
`UncheckedMessageException`s. This allows us to catch the exceptions locally
only where we believe we can recover.

### Error Strategies

The Mail Importer concept of an _error strategy_ allows the user to specify how
failures to import a message into Gmail should be dealt with. The bundled
strategies are, `Ignore`, `Fail`, `Retry` and `SaveForLater`. The `SaveForLater`
strategy copies the message to a new `Folder` in a new local mailbox. This
ensures that we don't have to modify the original mail store.

Some error strategies can also be layered. For example, `Retry` will retry
exactly once. So the compound error strategy `Retry,Retry,SaveForLater` will
cause the upload to be retried twice and then the message will be saved to a
new folder and processing will continue.

Note that error strategies are designed to help when uploads to Gmail fail;
when the local store can't be read, there's not a lot that Mail Importer can do.

### Guice Usage

The Mail Importer uses [Guice](https://github.com/google/guice) to wire all of
the pieces together. In theory, each module should be reusable and independent.
However the command-line arguments parsing is monolithic and we end up passing
around a giant `CommandLineArguments` instance that holds all the flags. (See
the Future Improvements section below.)

Each module should bind providers that extract the individual parts of the
`CommandLineArguments` object that are necessary and those should be injected
in the module's classes.

### Logging

We also use Guice's automatic injection of `Logger`s so that each class has a
logger. We use the `java.util.logging` classes because there is no compelling
reason to use anything else. For other libraries, we configure them to do the
same.

Log levels are defined as:

 * finest: Log statements in tight loops that are only used for detailed
   debugging. These should rarely be enabled.
 * fine: Log statements for tracing execution to figure out what code paths
   are being executed. These should only seldom be enabled.
 * info: Log statement marking the progress of a task. These can be enabled
   frequently.
 * warning: Unexpected or erroneous behavior where the application can
   reasonably continue. These should always be enabled.
 * severe: Unexpected or erroneous behavior that causes the application to
   stop functioning. These should always be enabled.

## Future Improvements

### Adding a GUI

The Mail Importer is currently a command-line tool targeted and developers, IT
professionals, and tech-savvy users. It would be great to add a graphical
interface that would allow "regular" users to use it. Ideally, it would be
packaged as a JNLP app that could be run directly from the Web.

### Parallelizing Local and Remote Processing

Currently the Mail Importer makes no attempt to do local and remote requests in
parallel. This is because, in general, local processing is much, much faster
than remote requests making any potential speedup irrelevant when compared with
the whole time that the import takes.

### Forcing All Messages to be Uploaded

The Mail Importer currently assumes that all messages with the same RFC822
message ID in the local store are the same message. As stated above, this is to
save re-uploading the message contents multiple times when imports are
interrupted or the message appears in multiple local folders.

We could add a flag that forces all messages to be uploaded. Gmail _should_
de-dupe the messages if they are truly equivalent.

Note that Gmail has two modes of uploading messages: _insert_ and _upload_.
Messages uploaded through the _insert_ API do not have de-duping and other
Gmail processing logic run on them. Generally, using _upload_ is better.

### Closing `Folder`s

The current code makes no attempt to close the `Folder`s opened during
processing. This is bad because Mstor can cache messages and whatnot in memory
leading to more memory usage than strictly necessary.

Why is no attempt made to close folders? In the current implementation, the
`LocalStorage` implements `Iteratable<LocalMessage>`. This doesn't give any
feedback about whether messages in a given folder are still being processed or
anything like that.

This may not be as bad as it sounds as, in theory, the folder should be garbage
collected once it is unreferenced. This will also free Mstor's message cache
for the folder.

### Modularize Command Line Argument Processing

Right now there is one giant object that has all of the command-line arguments.
Yuck. Args4J can actually be parameterized to build multiple command-line
arguments-holding objects based on annotations. We haven't done this because
it's basically a project in itself.

### Use History Records to Stay in Sync with Gmail

We currently assume that Mail Importer is the only user of the account in
question and that things like labels and message state will stay constant
throughout the run. It would be better to actually query the history or get
changes pushed down to Mail Importer while it is running so that it can keep
its internal state in sync with the server. This will probably require a
redesign.

### Convert from Guice to Dagger 2.0

While awesome, Guice has some flaws, one of the largest of which is that there
is no easy way to declare dependencies and contracts between modules. Dagger
promises to make this better. Dagger also promises to be more debuggable.

### Convert to SLF4J

Maybe it would be good to convert to SLF4J?

### Import Outlook PST/OST format

MS open sourced the file formats around five years ago, so allowing imports
from this widespread email client would be a nice feature.

