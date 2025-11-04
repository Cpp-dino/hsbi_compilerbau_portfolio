import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.tree.ParseTree;
import pp.PrettyParserGrammerParser.ProgramContext;
import org.antlr.v4.gui.Trees;
import javax.swing.*;

// liest Quellcode-Datei ein und erzeugt Parse-Tree mit ANTLR gemäß Grammatik
public class Main {

  // Verzeichnis für Quelltext-Beispiele
  static final String SOURCE_DIR = "/Users/marcelotzen/Desktop/cb_2/src/main/resources/pp/";

  // Quelltext aus gewünschter Datei gleichmäßig formattiert ausgeben
  static void main(String... args) throws IOException, URISyntaxException {

    // welche Datei soll verarbeitet werden?
    String targetFile = IO.readln("\nDateiname: ");

    // Quellcode aus Datei einlesen
    String sourceText = null;

    try {
      Path path = Paths.get(SOURCE_DIR + targetFile);
      sourceText = Files.readString(path, StandardCharsets.UTF_8);
    }
    // Datei nicht gefunden --> abbrechen
    catch (IOException exception) {
      System.out.println("Kein Quelltext unter dem Dateinamen \"" + targetFile + "\" gefunden.\n");
      return;
    }

    // Quellcode ausgeben
    IO.println("\n\n------ " + targetFile + " ------");
    IO.println(sourceText);
    IO.println("\n\n------ Parse-Tree ------\n");

    // Quellcode lexen
    pp.PrettyParserGrammerLexer lexer =
        new pp.PrettyParserGrammerLexer(CharStreams.fromString(sourceText));
    CommonTokenStream tokens = new CommonTokenStream(lexer);

    // Tokens parsen
    pp.PrettyParserGrammerParser parser = new pp.PrettyParserGrammerParser(tokens);
    ParseTree tree = parser.program();

    // Parse-Tree ausgeben
    IO.println(tree.toStringTree(parser));
    IO.println("\n");

    Trees.inspect(tree, parser);

    // Quelltext gleichmäßig formattiert ausgeben
    IO.println("\n\n------ formattierter Quelltext ------\n");
    PrettyPrinter.prettyPrint(tree, parser);
    IO.println("\n");

    // als AST ausgeben
    lexer = new pp.PrettyParserGrammerLexer(CharStreams.fromString(sourceText));
    tokens = new CommonTokenStream(lexer);
    parser = new pp.PrettyParserGrammerParser(tokens);
    ProgramContext program = parser.program();

    List<PrettyPrinterAST.Anweisung> ast = PrettyPrinterAST.toAst(program);
    IO.println(ast);

    // formattiert auf Basis des AST ausgeben
    PrettyPrinter.prettyPrint(ast);

  }
}
