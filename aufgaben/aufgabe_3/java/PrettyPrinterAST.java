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
