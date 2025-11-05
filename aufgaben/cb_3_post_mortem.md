# Zusammenfassung
In der ersten Aufgabe habe ich zunächst für die gegebene Sprache eine ANTLR-Grammatik erstellt, also erst die Lexer-, dann die Parser-Regeln umgesetzt. Anschließend wurde auf Basis der Grammatik ein Lexer und Parser generiert (automatisch von ANTLR), um syntaktisch korrekte Programme in Parse-Trees zu überführen. Damit habe ich dann einen Pretty-Printer implementiert, der den Parse-Tree durchgeht und den Quelltext wieder einheitlich eingerückt ausgibt. Abschließend wurde eine Konvertierung von Parse-Tree zum AST entwickelt, indem der Parse-Tree travesiert wird, dabei in eine kompaktere, weniger redundante, und für die Ausgabe geeignete Struktur transformiert, und der Pretty-Printer dementsprechend angepasst.

# Details
Für die erste Aufgabe habe ich zunächst die relevanten Symbolen / Tokens aus der Aufgabenstellung gelesen, damit dann entsprechende Lexe-Regeln in der ANTLR-Grammatik erstellt. Auf dieser Grundlage habe ich die beschriebenen Regeln für die Sprache als Parser-Regeln umgesetzt. Beim Pretty Printer habe ich einfach alle Tokens ausgegeben, dabei die Nichterminale ("program", "zuweisung", etc.) explizit ignoriert. Mit jedem "do" habe ich die Einrück-Tiefe inkrementiert, mit jedem "end" entsprechend krementiert. In der letzten Aufgabe habe ich die Travesierung der Parse-Trees mit Pattern-Matching und einer zugehörigen Hirachie von records implementiert. Für die Konvertierung von Parse-Tree zu AST habe ich zuerst die records-Hirachie deifiniert, danach die Funktionen zur Travesierung implementiert. 

# Herausforderungen
Herausfordern war zunächst, das ich neben Leerzeichen und Tabs auch Newlines mit -> skip in einer entsprechenden Lexer-Regel ignoriert habe. Damit konnte jeddoch nicht die Anfoderung an abschließende Leerzeichen nach Anweisungen erfüllt werden, daher habe ich diese im Lexer nicht ignoriert, sondern im Parser entsprechend behandelt - in der Lösung sind daher beliebige Sequenzen aus Newlines zwischen den einzelnen Tokens explizit erlaubt. Schwierig war zunächst auch die korrekte Umsetzung des Vorrangs der verschiednenen Operatoren untereinandern.

# Reflexion
Durch die Aufgabe habe ich ein tieferes Verständnis für den Unterschied zwischen Parse-Tree und AST gewonnen, die Transformation war sehr spannend, weil hier ja überflüssige Verschachtelungen ignoriert werden und nur die semantisch relevanten Knoten beibehalten wurden, was die Ausgabe wesentlich vereinfacht. Zudem konnte ich praktische Erfahrungen in ANTLR sammeln und insbesondere Traversierung von Parse-Trees und ASTs am Beispiel von konsitenter Pretty-Printing-Ausgaben besser nachvollziehen.

# Link zur Lösung

[Direktlink zur Lösung](https://github.com/Cpp-dino/hsbi_compilerbau_portfolio/blob/73341fea957f08415f9be9d1d7aa0a8e563c17ea/aufgaben/cb_3.md)
