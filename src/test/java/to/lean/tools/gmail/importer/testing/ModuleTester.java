/*
 * Copyright 2015 The Mail Importer Authors. All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package to.lean.tools.gmail.importer.testing;

import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Key;
import com.google.inject.Module;
import com.google.inject.spi.DefaultElementVisitor;
import com.google.inject.spi.Element;
import com.google.inject.spi.Elements;
import com.google.inject.spi.ProviderLookup;
import com.google.inject.util.Providers;
import java.util.ArrayList;
import java.util.List;

/** Created by flan on 9/5/15. */
public class ModuleTester {
  private Module module;

  public ModuleTester(Module module) {
    this.module = module;
  }

  public void assertAllDependenciesDeclared() {
    List<Key> requiredKeys = new ArrayList<>();

    List<Element> elements = Elements.getElements(module);
    for (Element element : elements) {
      element.acceptVisitor(
          new DefaultElementVisitor<Void>() {
            @Override
            public <T> Void visit(ProviderLookup<T> providerLookup) {
              // Required keys are the only ones with null injection points.
              if (providerLookup.getDependency().getInjectionPoint() == null) {
                requiredKeys.add(providerLookup.getKey());
              }
              return null;
            }
          });
    }

    Injector injector =
        Guice.createInjector(
            module,
            new AbstractModule() {
              @Override
              @SuppressWarnings("unchecked")
              protected void configure() {
                binder().disableCircularProxies();
                binder().requireAtInjectOnConstructors();
                binder().requireExactBindingAnnotations();

                for (Key<?> key : requiredKeys) {
                  bind((Key) key).toProvider(Providers.of(null));
                }
              }
            });

    injector.getAllBindings();
  }
}
