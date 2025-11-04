grammar PrettyParserGrammer;

@header {
package pp;
}



// ---------------------------
//     - - - Parser - - -
// ---------------------------

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



// ---------------------------
//     - - - Lexer - - -
// ---------------------------

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
