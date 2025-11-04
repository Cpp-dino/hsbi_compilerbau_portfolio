import java.util.Arrays;
import java.util.Iterator;
import org.antlr.v4.runtime.tree.ParseTree;
import org.antlr.v4.runtime.tree.Trees;
import java.util.List;

// ParseTree gleichmäßig formattiert ausgeben
class PrettyPrinter {

  // Breite einer Einrückung
  static final int TAB_SIZE = 4;

  // Parser- / Lexer-Symbole (bei Ausgabe ignorieren)
  static final String IGNORE_SYMBOLS[] = {
    "program",
    "anweisung",
    "zuweisung",
    "ausdruck",
    "summe",
    "produkt",
    "faktor",
    "vergleich",
    "gleichung",
    "ungleichung",
    "literal",
    "if_",
    "while_",
    "\n",
    "<EOF>"
  };

  // Hilfs-Überladung für 'prettyPrint()', mit ParseTree
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

  // ParseTree gleichmäßig formattiert ausgeben
  public static void prettyPrint(ParseTree tree, pp.PrettyParserGrammerParser parser) {
    prettyPrint(tree, parser, 0);
  }

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
}
