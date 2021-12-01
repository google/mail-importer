# Mail Importer for Gmail

Do you have an old Thunderbird mail archive that you POP3ed down from AOL?

Do you want to move those old messages to Gmail so that you can use the Gmail
app on your phone and still have access to everything?

If so, maybe _Mail Importer for Gmail_ is for you!

If you are trying to bulk import mbox format files to Google Workspace,
you should probably look at
[import-mailbox-to-gmail](https://github.com/google/import-mailbox-to-gmail).

**DISCLAIMER**: This is not an official Google product.

## What does it do?

_Mail Importer for Gmail_ will upload the contents of a Thunderbird mail
archive to Gmail and do its best to preserve the read state, flagged state, and
folders of the messages. As messages are uploaded verbatim, Gmail will have an
exact copy, including all attachments and headers.

_Mail Importer_ also makes sure to only upload messages that aren't already in
Gmail. This makes it easy to re-run the import multiple times if something goes
wrong.

## How can I run it?

Currently, _Mail Importer for Gmail_ is in early development. It is __not__
user friendly in any way. If you are not a developer, you probably want to
stay away.

### Getting a Client Secret

Each developer needs a _client secret_ to identify their version of Mail
Importer to Google using
[OAuth2](https://developers.google.com/identity/protocols/OAuth2). To get a
client secret, you have to create a project in the
[Google Developers Console](https://github.com/googleads/googleads-dotnet-lib/wiki/How-to-create-OAuth2-client-id-and-secret),
configure it correctly, then download the generated credentials. The result
should be a JSON file named something like:

```
client_secret_729820383-898athoe9t33ntohuoc.apps.googleusercontent.com.json
```

You need to copy this to `src/main/resources/client_secret.json` in your Mail
Importer project. _**Never check in this file!**_ It is _your_ key and no one
else's.

### Building Mail Importer

Mail Importer uses [Maven](https://maven.apache.org/). This means that it will
download all of the necessary dependencies automatically when you build it like
this:

```
mvn clean package assembly:single
```

This will produce a runnable `.jar` file in
`target/mail-importer-1.0-SNAPSHOT-jar-with-dependencies.jar`.

### Running Mail Importer

Once Mail Importer is built, you can run it like:

```
java -jar ./target/mail-importer-1.0-SNAPSHOT-jar-with-dependencies.jar \
    --mailbox DIRECTORY
```

where `DIRECTORY` is the Thunderbird _mailbox_ to open.

Note that the Thunderbird `Mail` directory usually has several sub-directories
called `ImapMail`, `OfflineCache`, and `Mail`. Then under `Mail`, you should
find individual accounts, like `pop.mail.yahoo.com` or `pop.csi.com`, and
`Local Folders`. It is this last level of directory that contains the actual
_mailbox_.

For example, if you had old CompuServe mail on a Mac, you might run Mail
Importer like this:

```
java -jar ./target/mail-importer-1.0-SNAPSHOT-jar-with-dependencies.jar \
    --mailbox /Users/me/Library/Thunderbird/my_profile/Mail/pop.csi.com
```

## Can I help make _Mail Importer_ better?

You bet! See the [CONTRIBUTING.md](CONTRIBUTING.md) file for more information.
