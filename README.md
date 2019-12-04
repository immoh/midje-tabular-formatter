# midje-tabular-formatter [![CircleCI](https://circleci.com/gh/immoh/midje-tabular-formatter/tree/master.svg?style=svg)](https://circleci.com/gh/immoh/midje-tabular-formatter/tree/master)

Tool for formatting [Midje](https://github.com/marick/Midje) [tabular facts](https://github.com/marick/Midje/wiki/Tabular-facts).

Turns this:

```clojure
(tabular
  (fact "The rules of Conway's life"
        (alive? ?cell-status ?neighbor-count) => ?expected)
  ?cell-status ?neighbor-count ?expected
    :alive 1 FALSEY
  :alive 2 truthy
   :alive 3 truthy
  :alive 4 FALSEY

  :dead 2 FALSEY
  :dead 3 truthy
  :dead 4 FALSEY)
```

into this:

```clojure
(tabular
  (fact "The rules of Conway's life"
        (alive? ?cell-status ?neighbor-count) => ?expected)
  ?cell-status ?neighbor-count ?expected
  :alive       1               FALSEY
  :alive       2               truthy
  :alive       3               truthy
  :alive       4               FALSEY
  :dead        2               FALSEY
  :dead        3               truthy
  :dead        4               FALSEY)
```

## Usage

### tools.deps

Use this one liner:

```
clojure -Sdeps '{:deps {midje-tabular-formatter {:mvn/version "0.1.0-SNAPSHOT"}}}' -m midje-tabular-formatter.main
```

Or add it as an alias in your `deps.edn` file:

```clojure
{:aliases {:formatter {:extra-deps {midje-tabular-formatter {:mvn/version "0.1.0-SNAPSHOT"}}
                       :main-opts  ["-m" "midje-tabular-formatter.main"]}}}
```

and run with:

```
clojure -Aformatter
```

### Leiningen

Add dependency and alias to `project.clj` or `.lein/profiles.clj`:

```clojure
{:dependencies [[midje-tabular-formatter "0.1.0-SNAPSHOT"]]
 :aliases      {"format-tables" ["run" "-m" "midje-tabular-formatter.main"]}}
```

and run with:

```
lein format-tables
```

### REPL

Include dependency in your classpath and call

```clojure
(require '[midje-tabular-formatter.main :as main])
=> nil
(main/format-tables! "/path/to/myproject")
=> {:success? true, :summary "Checked 51 files, all fine."}
```

There's also lower-level API that deals with strings:

```clojure
(require '[midje-tabular-formatter.core :as formatter])
=> nil
(println (formatter/format-tables "
(tabular
  (fact ?foo => 1)
  ?foo 1)"))
;; prints:
(tabular
  (fact ?foo => 1)
  ?foo
  1)
=> nil
```

## License

Copyright © 2019 Immo Heikkinen

This program and the accompanying materials are made available under the
terms of the Eclipse Public License 2.0 which is available at
http://www.eclipse.org/legal/epl-2.0.

This Source Code may also be made available under the following Secondary
Licenses when the conditions for such availability set forth in the Eclipse
Public License, v. 2.0 are satisfied: GNU General Public License as published by
the Free Software Foundation, either version 2 of the License, or (at your
option) any later version, with the GNU Classpath Exception which is available
at https://www.gnu.org/software/classpath/license.html.
