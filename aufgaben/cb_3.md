# A3.1: Grammatik

**ParseTree zu *test_1.pp*:**

![ParseTree zu *test_1.pp*](https://github.com/Cpp-dino/hsbi_compilerbau_portfolio/blob/04c3b36f59aa3f0d4f79ca1f0666954131359114/images/cb2_tree2.png)


**ParseTree zu *test_2.pp*:**

![ParseTree zu *test_2.pp*](https://github.com/Cpp-dino/hsbi_compilerbau_portfolio/blob/04c3b36f59aa3f0d4f79ca1f0666954131359114/images/cb2_tree3.png)


**ParseTree zu *test_3.pp*:**

![ParseTree zu *test_3.pp*](https://github.com/Cpp-dino/hsbi_compilerbau_portfolio/blob/04c3b36f59aa3f0d4f79ca1f0666954131359114/images/cb2_tree1.png)


### **Parser:**

``` antlr
// START - Programm aus Anweisungen (getrennt durch Newlines):
program     : '\n'* (block+=anweisung '\n'+)*? schluss=anweisung '\n'* EOF ;

// Zuweisung / Kontrollstruktur / Schleife:
anweisung   : zuweisung         # ZuweisungAnweisung
            | while_            # WhileAnweisung
            | if_               # IfAnweisung ;    


// Zuweisung:
zuweisung   : bez=BEZEICHNER '\n'* ':=' '\n'* wert=ausdruck ;

// Schleife:
while_      : WHILE '\n'* bdg=vergleich '\n'* DO '\n'+
              (block+=anweisung '\n'+)+
              END ;

// Kontrollstruktur:
if_         : IF '\n'* bdg=vergleich '\n'* DO '\n'+
              (ifBlock+=anweisung '\n'+)+
              (ELSE '\n'* DO '\n'+ (elseBlock+=anweisung '\n'+)+)?
              END ;


// arithmetischer Ausdruck:
// Produkt / Wert / Summe von (Produkten / Werten)
// --> Punkt vor Strich
ausdruck    : summe ;

summe       : produkt                                           # EinzelSumme
            | op1=produkt '\n'* ('+' | '-') '\n'* op2=summe     # BinaerSumme ;

produkt     : faktor                                            # EinzelProdukt
            | op1=faktor '\n'* ('*' | '/') '\n'* op2=produkt    # BinaerProdukt ;


// Vergleichs-Ausdruck:
// Vergleich / Ausdruck / Gleichung von (Vergleichen / Ausdrücken)
// --> Vergleich vor Gleichung
vergleich   : gleichung ;

gleichung   : ungleichung                                               # EinzelGleichung
            | op1=ungleichung '\n'* ('==' | '!=') '\n'* op2=ungleichung # BinaerGleichung ;

ungleichung : ausdruck                                                  # EinzelUngleichung
            | op1=ausdruck '\n'* ('>' | '<') '\n'* op2=ausdruck         # BinaerUngleichung ;


// Variable / Literal:
faktor      : literal       # LiteralFaktor
            | BEZEICHNER    # BezeichnerFaktor ;

// Literal:
literal     : INT           # IntLiteral
            | STRING        # StringLiteral ;
```

### **Lexer:**
``` antlr
// Schlüsselwörter:
IF          : 'if' ;
DO          : 'do' ;
ELSE        : 'else' ;
WHILE       : 'while ' ;
END         : 'end' ;


// Bezeichner bestehen aus a-z, A-Z, 0-9, _, dürfen nicht mit einer Ziffer 0-9 beginnen:
BEZEICHNER  : [a-zA-Z_][a-zA-Z0-9_]* ;


// eine beliebige Folge der Ziffern 0-9:
INT         : [0-9]+ ;

// beliebig viele ASCII-Zeichen (außer "), eingeschlossen in "...":
STRING      : '"' (~["])* '"' ;


// Kommentare beginnen mit #, enden bei Newline (verwerfen mit '-> skip'):
COMMENT     : '#' (~[\n\r])* -> skip ;

// Whitespaces, dh. Tabs, Spaces (verwerfen mit '-> skip'):
WS          : [\t ]+ -> skip ;

```






# A3.2: Pretty Printer

siehe vollständigen Programmcode in: [aufgaben/aufgabe_3/java/](https://github.com/Cpp-dino/hsbi_compilerbau_portfolio/tree/d764dc66b9646983211cf811aa3970a36ff18c10/aufgaben/aufgabe_3/java)

``` java
  // ParseTree gleichmäßig formattiert ausgeben
  private static int prettyPrint(ParseTree tree, pp.PrettyParserGrammerParser parser, int depth) {
    // Parser-Symbol als String holen
    String node = Trees.getNodeText(tree, parser);
    int d = depth;

    // Ende der Einrückung bei Schleifen / Kontrollstrukturen
    if (node.matches("end") || node.matches("else")) {
      d -= 1;
    }

    // Einrückung
    String indent = " ".repeat(TAB_SIZE * d);

    if (node.matches("anweisung") || node.matches("else") || node.matches("end")) {
      System.out.print("\n" + indent);
    }

    // alle rein semantischen Symbole ignorieren
    if (!Arrays.asList(IGNORE_SYMBOLS).contains(node)) {
      System.out.print(node + " ");
    }

    // Einrückungen bei Schleifen / Kontrollstrukturen
    if (node.matches("do")) {
      d += 1;
    }

    for (int i = 0; i < tree.getChildCount(); i++) {
      d = prettyPrint(tree.getChild(i), parser, d);
    }

    return d;
  }
```


### Demonstration:


#### [*test_1.pp:*](https://github.com/Cpp-dino/hsbi_compilerbau_portfolio/blob/d764dc66b9646983211cf811aa3970a36ff18c10/aufgaben/aufgabe_3/test_1.pp)

``` plain
Dateiname: test_1.pp

------ test_1.pp ------

# Zuweisungen
a := "abc"
b := 42                             # b ist zunächst 42

# Kontrollstruktur
if a == "abc" do
    if b + 3 < 5 * 5 + 4 * 6 do     # Punkt vor Strich beachten!
        b := "wuppie"
    else do
        a := "nope"                 # a auf "nope" setzten
    end
else do
    b := 7                          # sonst: b auf 7 setzten
end

# Schleife
while b + 4 < 2 * b do
    b := b - 1                      # b dekrementieren

    if b / 2 == b + 10 do           # in dem Fall: b neu setzten
        b := b / 10 - 1
    end
end

------ Parse-Tree ------

(program \n \n (anweisung (zuweisung a \n \n := (ausdruck (summe (produkt (faktor (literal "abc"))))))) \n (anweisung (zuweisung b := (ausdruck (summe (produkt (faktor (literal 42))))))) \n \n \n (anweisung (if_ if \n \n (vergleich (gleichung (ungleichung (ausdruck (summe (produkt (faktor a))))) == (ungleichung (ausdruck (summe (produkt (faktor (literal "abc")))))))) \n do \n (anweisung (if_ if (vergleich (gleichung (ungleichung (ausdruck (summe (produkt (faktor b)) + (summe (produkt (faktor (literal 3)))))) < (ausdruck (summe (produkt (faktor (literal 5)) * (produkt (faktor (literal 5)))) + (summe (produkt (faktor (literal 4)) * (produkt (faktor (literal 6)))))))))) \n do \n \n (anweisung (zuweisung b := (ausdruck (summe (produkt (faktor (literal "wuppie"))))))) \n \n else \n \n do \n (anweisung (zuweisung a := (ausdruck (summe (produkt (faktor (literal "nope"))))))) \n end)) \n else do \n (anweisung (zuweisung b := \n \n (ausdruck (summe (produkt (faktor (literal 7))))))) \n end)) \n \n (anweisung (while_ while  \n (vergleich (gleichung (ungleichung (ausdruck (summe (produkt (faktor b)) + (summe (produkt (faktor (literal 4)))))) < (ausdruck (summe (produkt (faktor (literal 2)) * (produkt (faktor b)))))))) do \n (anweisung (zuweisung b := (ausdruck (summe (produkt (faktor b)) - (summe (produkt (faktor (literal 1)))))))) \n \n (anweisung (if_ if (vergleich (gleichung (ungleichung (ausdruck (summe (produkt (faktor b) \n / (produkt (faktor (literal 2))))))) \n == (ungleichung (ausdruck (summe (produkt (faktor b)) + (summe (produkt (faktor (literal 10))))))))) \n do \n (anweisung (zuweisung b := (ausdruck (summe (produkt (faktor b) / \n (produkt (faktor (literal 10)))) - (summe (produkt (faktor (literal 1)))))))) \n end)) \n end)) \n <EOF>)

------ formattierter Quelltext ------

a := "abc" 
b := 42 
if a == "abc" do 
    if b + 3 < 5 * 5 + 4 * 6 do 
        b := "wuppie" 
    else do 
        a := "nope" 
    end 
else do 
    b := 7 
end 
while  b + 4 < 2 * b do 
    b := b - 1 
    if b / 2 == b + 10 do 
        b := b / 10 - 1 
    end 
end
```

#### [*test_2.pp:*](https://github.com/Cpp-dino/hsbi_compilerbau_portfolio/blob/d764dc66b9646983211cf811aa3970a36ff18c10/aufgaben/aufgabe_3/test_2.pp)

``` plain
Dateiname: test_2.pp

------ test_2.pp ------
a     := 0
    if    10 < 1
       do
a    :=     42      # Zuweisung des Wertes 42 an die Variable a
else do
        a :=      7
  end

------ Parse-Tree ------

(program (anweisung (zuweisung a := (ausdruck (summe (produkt (faktor (literal 0))))))) \n (anweisung (if_ if (vergleich (gleichung (ungleichung (ausdruck (summe (produkt (faktor (literal 10))))) < (ausdruck (summe (produkt (faktor (literal 1)))))))) \n do \n (anweisung (zuweisung a := (ausdruck (summe (produkt (faktor (literal 42))))))) \n else do \n (anweisung (zuweisung a := (ausdruck (summe (produkt (faktor (literal 7))))))) \n end)) <EOF>)

------ formattierter Quelltext ------

a := 0 
if 10 < 1 do 
    a := 42 
else do 
    a := 7 
end
```

#### [*test_3.pp:*](https://github.com/Cpp-dino/hsbi_compilerbau_portfolio/blob/d764dc66b9646983211cf811aa3970a36ff18c10/aufgaben/aufgabe_3/test_3.pp)

``` plain
Dateiname: test_3.pp

------ test_3.pp ------

a
  :=
  2  # Variable a (Wert: 2)
b
  := # Variable a (Wert: 2)
  3   

if                              # komplexe Bedingung folgt:
 2*1  -1< 1 
  * 14/    2==    1
    < 22 
+
        a   -

91 
            *
 7/b
do
                                # Zuweisung (falls Bedingung zutrifft)
        c    
 :=           "wuppie"
        else                    # ansonten:
  do
c:=
  
    "nope"                      # "nope" als Wert für neue Variable c
        end

------ Parse-Tree ------

(program \n (anweisung (zuweisung a \n := \n (ausdruck (summe (produkt (faktor (literal 2))))))) \n (anweisung (zuweisung b \n := \n (ausdruck (summe (produkt (faktor (literal 3))))))) \n \n (anweisung (if_ if \n (vergleich (gleichung (ungleichung (ausdruck (summe (produkt (faktor (literal 2)) * (produkt (faktor (literal 1)))) - (summe (produkt (faktor (literal 1)))))) < (ausdruck (summe (produkt (faktor (literal 1)) \n * (produkt (faktor (literal 14)) / (produkt (faktor (literal 2)))))))) == (ungleichung (ausdruck (summe (produkt (faktor (literal 1))))) \n < (ausdruck (summe (produkt (faktor (literal 22))) \n + \n (summe (produkt (faktor a)) - \n \n (summe (produkt (faktor (literal 91)) \n * \n (produkt (faktor (literal 7)) / (produkt (faktor b))))))))))) \n do \n \n (anweisung (zuweisung c \n := (ausdruck (summe (produkt (faktor (literal "wuppie"))))))) \n else \n do \n (anweisung (zuweisung c := \n \n (ausdruck (summe (produkt (faktor (literal "nope"))))))) \n end)) <EOF>)

------ formattierter Quelltext ------

a := 2 
b := 3 
if 2 * 1 - 1 < 1 * 14 / 2 == 1 < 22 + a - 91 * 7 / b do 
    c := "wuppie" 
else do 
    c := "nope" 
end 

```



# A3.3: AST

**AST zu *test_1.pp*:**

![AST zu *test_1.pp*](https://github.com/Cpp-dino/hsbi_compilerbau_portfolio/blob/04c3b36f59aa3f0d4f79ca1f0666954131359114/images/cb2_ast2.png)


**AST zu *test_2.pp*:**

![AST zu *test_2.pp*](https://github.com/Cpp-dino/hsbi_compilerbau_portfolio/blob/04c3b36f59aa3f0d4f79ca1f0666954131359114/images/cb2_ast1.png)


#### **AST erstellen:**

siehe vollständigen Programmcode in: [aufgaben/aufgabe_3/java/](https://github.com/Cpp-dino/hsbi_compilerbau_portfolio/tree/d764dc66b9646983211cf811aa3970a36ff18c10/aufgaben/aufgabe_3/java)

``` java
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import pp.PrettyParserGrammerParser.*;

// ParseTree als AST konvertieren
public class PrettyPrinterAST {

  // Interface für Statements
  sealed interface Anweisung permits Anweisung.Zuweisung, Anweisung.While, Anweisung.If {
    record Zuweisung(Ausdruck.Bezeichner name, Ausdruck wert) implements Anweisung {}

    record While(Vergleich bedingung, List<Anweisung> block) implements Anweisung {}

    record If(Vergleich bedingung, List<Anweisung> ifBlock, List<Anweisung> elseBlock)
        implements Anweisung {}
  }

  // Interface für Vergleichs-Ausdrücke
  sealed interface Vergleich {
    record Gleichung(Vergleich links, Vergleich rechts) implements Vergleich {}

    record Ungleichung(Vergleich links, Vergleich rechts) implements Vergleich {}
  }

  // Interface für Arithmetische Ausdrücke
  sealed interface Ausdruck extends Vergleich
      permits Ausdruck.Bezeichner,
          Ausdruck.StringLiteral,
          Ausdruck.IntLiteral,
          Ausdruck.Summe,
          Ausdruck.Produkt {
    record Bezeichner(String name) implements Ausdruck {}

    record StringLiteral(String wert) implements Ausdruck {}

    record IntLiteral(int wert) implements Ausdruck {}

    record Summe(Ausdruck links, Ausdruck rechts) implements Ausdruck {}

    record Produkt(Ausdruck links, Ausdruck rechts) implements Ausdruck {}
  }

  // ParseTree als AST konvertieren
  static List<Anweisung> toAst(ProgramContext prg) {
    // letzte Anweisung mit Rest zusammenfassen
    List<AnweisungContext> anws = prg.block;
    anws.add(prg.schluss);

    return anws.stream()
        .map(PrettyPrinterAST::toAst)
        .collect(Collectors.toCollection(ArrayList::new));
  }

  // Anwweisung
  static Anweisung toAst(AnweisungContext anw) {
    return switch (anw) {
      // Zuweisung
      case ZuweisungAnweisungContext zwAnw -> toAst(zwAnw);
      // While
      case WhileAnweisungContext whileAnw -> toAst(whileAnw);
      // If-Else
      case IfAnweisungContext ifAnw -> toAst(ifAnw);
      default -> throw new IllegalStateException();
    };
  }

  // Zuweisung
  static Anweisung toAst(ZuweisungAnweisungContext anw) {
    // Bezeichner
    String name = anw.zuweisung().bez.getText();
    Ausdruck.Bezeichner bezeichner = new Ausdruck.Bezeichner(name);

    // Wert (arithmetischer Ausdruck)
    AusdruckContext wert = anw.zuweisung().wert;

    return new Anweisung.Zuweisung(bezeichner, toAst(wert));
  }

  // While
  static Anweisung toAst(WhileAnweisungContext anw) {
    // Bedingung (Vergleichs-Ausdruck)
    VergleichContext bdg = anw.while_().bdg;

    // Anweisungsblock
    List<AnweisungContext> blockContext = anw.while_().block;
    List<Anweisung> block = blockContext.stream().map(PrettyPrinterAST::toAst).toList();

    return new Anweisung.While(toAst(bdg), block);
  }

  // If-Else
  static Anweisung toAst(IfAnweisungContext anw) {
    // Bedingung (Vergleichs-Ausdruck)
    VergleichContext bdg = anw.if_().bdg;

    // If-Anweisungsblock
    List<AnweisungContext> ifBlockContext = anw.if_().ifBlock;
    List<Anweisung> ifBlock = ifBlockContext.stream().map(PrettyPrinterAST::toAst).toList();

    // Else-Anweisungsblock
    List<AnweisungContext> elseBlockContext = anw.if_().elseBlock;
    List<Anweisung> elseBlock = elseBlockContext.stream().map(PrettyPrinterAST::toAst).toList();

    return new Anweisung.If(toAst(bdg), ifBlock, elseBlock);
  }

  // Ausdruck (also Summe)
  static Ausdruck toAst(AusdruckContext aus) {
    // 'ausdruck' ist zunächst immer 'summe'
    return toAst(aus.summe());
  }

  // Summe
  static Ausdruck toAst(SummeContext sum) {
    return switch (sum) {
      // Produkt / Wert
      case EinzelSummeContext einzel -> toAst(einzel.produkt());
      // Summe
      case BinaerSummeContext binaer -> {
        yield new Ausdruck.Summe(toAst(binaer.op1), toAst(binaer.op2));
      }
      default -> throw new IllegalStateException();
    };
  }

  // Produkt
  static Ausdruck toAst(ProduktContext prod) {
    return switch (prod) {
      // Wert
      case EinzelProduktContext einzel -> toAst(einzel.faktor());
      // Produkt
      case BinaerProduktContext binaer -> {
        yield new Ausdruck.Produkt(toAst(binaer.op1), toAst(binaer.op2));
      }
      default -> throw new IllegalStateException();
    };
  }

  // Faktor
  static Ausdruck toAst(FaktorContext fakt) {
    return switch (fakt) {
      // Literal
      case LiteralFaktorContext literal -> toAst(literal.literal());
      // Bezeichner
      case BezeichnerFaktorContext name -> new Ausdruck.Bezeichner(name.BEZEICHNER().getText());
      default -> throw new IllegalStateException();
    };
  }

  // Literal
  static Ausdruck toAst(LiteralContext lit) {
    return switch (lit) {
      // Literal
      case IntLiteralContext intg ->
          new Ausdruck.IntLiteral(Integer.parseInt(intg.INT().getText()));
      // Bezeichner
      case StringLiteralContext str -> new Ausdruck.StringLiteral(str.STRING().getText());
      default -> throw new IllegalStateException();
    };
  }

  // Vergleichs-Ausdruck (also Gleichung)
  static Vergleich toAst(VergleichContext vergl) {
    // 'vergleich' ist zunächst immer 'gleichung'
    return toAst(vergl.gleichung());
  }

  // Gleichung
  static Vergleich toAst(GleichungContext gl) {
    return switch (gl) {
      // Produkt / Wert
      case EinzelGleichungContext einzel -> toAst(einzel.ungleichung());
      // Summe
      case BinaerGleichungContext binaer -> {
        yield new Vergleich.Gleichung(toAst(binaer.op1), toAst(binaer.op2));
      }
      default -> throw new IllegalStateException();
    };
  }

  // Ungleichung
  static Vergleich toAst(UngleichungContext prod) {
    return switch (prod) {
      // Wert
      case EinzelUngleichungContext einzel -> toAst(einzel.ausdruck());
      // Produkt
      case BinaerUngleichungContext binaer -> {
        yield new Ausdruck.Ungleichung(toAst(binaer.op1), toAst(binaer.op2));
      }
      default -> throw new IllegalStateException();
    };
  }
}
```

#### **geänderter Pretty-Printer:**

siehe vollständigen Programmcode in: [aufgaben/aufgabe_3/java/](https://github.com/Cpp-dino/hsbi_compilerbau_portfolio/tree/d764dc66b9646983211cf811aa3970a36ff18c10/aufgaben/aufgabe_3/java)

``` java
 // Hilfs-Überladung für 'prettyPrint()', mit AST
    private static void prettyPrint(List<PrettyPrinterAST.Anweisung> ast, int depth) {
        // alle Anweisungen durchgehen
        Iterator<PrettyPrinterAST.Anweisung> it = ast.iterator();

        while(it.hasNext()) {
            // Einrückung
            String indent = " ".repeat(TAB_SIZE * depth);
            System.out.print(indent);

            // Anweisung ausgeben
            PrettyPrinterAST.Anweisung anw = it.next();
            printAnweisung(anw, depth);
        }
    }

    // einzelne Anweisung ausgeben
    private static void printAnweisung(PrettyPrinterAST.Anweisung anw, int depth) {
        // Einrückung
        String indent = " ".repeat(TAB_SIZE * depth);

        // Zuweisung
        if (anw instanceof PrettyPrinterAST.Anweisung.Zuweisung) {
            PrettyPrinterAST.Anweisung.Zuweisung zuw = (PrettyPrinterAST.Anweisung.Zuweisung)anw;
            System.out.print(zuw.name().name() + " := ");
            printAusdruck(zuw.wert());
            System.out.println();
        }

        // While
        else if (anw instanceof PrettyPrinterAST.Anweisung.While) {
            PrettyPrinterAST.Anweisung.While loop = (PrettyPrinterAST.Anweisung.While)anw;

            System.out.print("while ");
            printVergleich(loop.bedingung());
            System.out.println(" do");

            prettyPrint(loop.block(), depth + 1);
            System.out.println(indent + "end");
        }

        // If-Else 
        else if (anw instanceof PrettyPrinterAST.Anweisung.If) {
            PrettyPrinterAST.Anweisung.If ifElse = (PrettyPrinterAST.Anweisung.If)anw;

            System.out.print("if ");
            printVergleich(ifElse.bedingung());
            System.out.println(" do");

            prettyPrint(ifElse.ifBlock(), depth + 1);

            if (!ifElse.elseBlock().isEmpty()) {
                System.out.println(indent + "else do");
                prettyPrint(ifElse.elseBlock(), depth + 1);
            }

            System.out.println(indent + "end");
        }
    }

    // Ausdruck ausgeben
    private static void printAusdruck(PrettyPrinterAST.Ausdruck aus) {
        // Bezeichner
        if (aus instanceof PrettyPrinterAST.Ausdruck.Bezeichner) {
            PrettyPrinterAST.Ausdruck.Bezeichner name = (PrettyPrinterAST.Ausdruck.Bezeichner)aus;
            System.out.print(name.name());
        }
        
        // String-Literal
        if (aus instanceof PrettyPrinterAST.Ausdruck.StringLiteral) {
            PrettyPrinterAST.Ausdruck.StringLiteral str = (PrettyPrinterAST.Ausdruck.StringLiteral)aus;
            System.out.print(str.wert());
        }

        // Int-Literal
        if (aus instanceof PrettyPrinterAST.Ausdruck.IntLiteral) {
            PrettyPrinterAST.Ausdruck.IntLiteral intg = (PrettyPrinterAST.Ausdruck.IntLiteral)aus;
            System.out.print(intg.wert());
        }

        // Summe
        if (aus instanceof PrettyPrinterAST.Ausdruck.Summe) {
            PrettyPrinterAST.Ausdruck.Summe summe = (PrettyPrinterAST.Ausdruck.Summe)aus;

            printAusdruck(summe.links());
            System.out.print(" + ");
            printAusdruck(summe.rechts());
        }

        // Produkt
        if (aus instanceof PrettyPrinterAST.Ausdruck.Produkt) {
            PrettyPrinterAST.Ausdruck.Produkt prod = (PrettyPrinterAST.Ausdruck.Produkt)aus;

            printAusdruck(prod.links());
            System.out.print(" * ");
            printAusdruck(prod.rechts());
        }
    }

    // Vergleich ausgeben
    private static void printVergleich(PrettyPrinterAST.Vergleich aus) {
        // Gleichung
        if (aus instanceof PrettyPrinterAST.Vergleich.Gleichung) {
            PrettyPrinterAST.Vergleich.Gleichung summe = (PrettyPrinterAST.Vergleich.Gleichung)aus;

            printVergleich(summe.links());
            System.out.print(" == ");
            printVergleich(summe.rechts());
        }

        // Ungleichung
        else if (aus instanceof PrettyPrinterAST.Vergleich.Ungleichung) {
            PrettyPrinterAST.Vergleich.Ungleichung prod = (PrettyPrinterAST.Vergleich.Ungleichung)aus;
            
            printVergleich(prod.links());
            System.out.print(" < ");
            printVergleich(prod.rechts());
        }

        // ansonten reiner Ausdruck
        else {
            printAusdruck((PrettyPrinterAST.Ausdruck)aus);
        }
    }

    // AST gleichmäßig formattiert ausgeben
    public static void prettyPrint(List<PrettyPrinterAST.Anweisung> ast) {
        prettyPrint(ast, 0);
    }
```









