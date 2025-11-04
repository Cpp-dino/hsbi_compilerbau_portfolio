# [A1.1: Sprachen von regulären Ausdrücken](https://github.com/Compiler-CampusMinden/CB-Vorlesung-Bachelor-W25/blob/master/homework/sheet01.md#a11-sprachen-von-regulären-ausdrücken-1p)

$`a\ +\ a\ (a\ +\ b)^*\ a`$

Regex beschreibt folgende Sprache:
- Wort beginnt mit `a`
- Wort endet mit `a`
- dazwischen beliebig viele `a` und `b`
- alternativ nur einzelnes `a` als Wort
 


# [A1.2: Bezeichner in Programmiersprachen](https://github.com/Compiler-CampusMinden/CB-Vorlesung-Bachelor-W25/blob/master/homework/sheet01.md#a12-bezeichner-in-programmiersprachen-3p)

## Regex:

$`((A-Z\ +\ a-z)\ \ (A-Z\ +\ a-z\ +\ 0-9\ +\ \_)^*\ \ (A-Z\ +\ a-z\ +\ 0-9))`$

`p_Val1_1`:
```
p_Val1_1	∈ L((A-Z + a-z) (A-Z + a-z + 0-9 + _)* (A-Z + a-z + 0-9))
	p 		    ∈ L(A-Z + a-z)
	_Val1_		∈ L((A-Z + a-z + 0-9 + _)*)
		_ 		    ∈ L(A-Z + a-z + 0-9 + _)
		V 		    ∈ L(A-Z + a-z + 0-9 + _)
		a 		    ∈ L(A-Z + a-z + 0-9 + _)
		l 		    ∈ L(A-Z + a-z + 0-9 + _)
		1 		    ∈ L(A-Z + a-z + 0-9 + _)
		_ 		    ∈ L(A-Z + a-z + 0-9 + _)
	l 		    ∈ L(A-Z + a-z + 0-9)
```

`vState_2`:
```
vState_2	∈ L((A-Z + a-z) (A-Z + a-z + 0-9 + _)* (A-Z + a-z + 0-9))
  v 		    ∈ L(A-Z + a-z)
  State_		∈ L((A-Z + a-z + 0-9 + _)*)
    S              ∈ L(A-Z + a-z + 0-9 + _)
    t 		       ∈ L(A-Z + a-z + 0-9 + _)
    a 		       ∈ L(A-Z + a-z + 0-9 + _)
    t 		       ∈ L(A-Z + a-z + 0-9 + _)
    e 		       ∈ L(A-Z + a-z + 0-9 + _)
    _ 		       ∈ L(A-Z + a-z + 0-9 + _)
  2         ∈ L(A-Z + a-z + 0-9)
```



## DFA:

<img src="https://github.com/Cpp-dino/hsbi_compilerbau_portfolio/blob/f057abcd18cf37aac6f6089f5e0e92c12efd558c/images/compiler1_1.png" width="100%">

`p_Val1_1`:

	S  -p->  p1  -_->  p1  -V->  p1  -a->  p1  -l->  p1  -1->  p1  -_->  p1  -1->  p2

`vState_2`:

	S  -v->  p1  -S->  p1  -t->  p1  -a->  p1  -t->  p1  -e->  p1  -_->  p1  -2->  p2



## Reguläre Grammatik:

```
Start  => (v FZ | V FZ | p FZ | P FZ | a FZ | ... | z FZ | A FZ | ... | Z FZ |
           v EZ | V EZ | p EZ | P EZ | a EZ | ... | z EZ | A EZ | ... | Z EZ)

FZ     => (a FZ | ... | z FZ | A FZ | ... | Z FZ | 0 FZ | ... | 9 FZ | _ FZ |
           a EZ | ... | z EZ | A FZ | ... | Z EZ | 0 EZ | ... | 9 EZ | _ EZ)

EZ 	   => (a Ende | ... | z Ende | A Ende | ... | Z Ende | 0 Ende | ... | 9 Ende)

Ende   => ε
```

`p_Val1_1`:

                        Start
                         / \
                        p   FZ
                            / \
                          _   FZ
                              / \
                            V   FZ
                                / \
                              a   FZ
                                  / \
                                l   FZ
                                    / \
                                  1   FZ
                                      / \
                                    _   EZ
                                        / \
                                      1   Ende
                                           |
                                           ε

`vState_2`:

                        Start
                         / \
                        v   FZ
                            / \
                          S   FZ
                              / \
                            t   FZ
                                / \
                              a   FZ
                                  / \
                                t   FZ
                                    / \
                                  e   FZ
                                      / \
                                  _   EZ
                                        / \
                                      2   Ende
                                           |
                                           ε



# [A1.3: Gleitkommazahlen in Programmiersprachen](https://github.com/Compiler-CampusMinden/CB-Vorlesung-Bachelor-W25/blob/master/homework/sheet01.md#a13-gleitkommazahlen-in-programmiersprachen-2p)

## Python:

### Regex:

$`(-\ +\ ε)\ \ ((Ziffern\ .\ Ziffern)\ \ +\ \ (.\ Ziffern)\ +\ (Ziffern\ .))\ \ ((-\ +\ ε)\ \ (e\ +\ E)\ \ Ziffern)\ \ +\ \ ε)`$

$`    Ziffern = (0-9) ((\_ + ε) (0-9))^*`$
        
`-.1_3e-0_2`:
```
-.1_3e-0_2	∈  L((- + ε) ((Ziffern . Ziffern) + (. Ziffern) + (Ziffern .)) ((- + ε) (e + E) Ziffern) + ε))
	- 			∈  L(- + ε)
	.1_3		∈  L((Ziffern . Ziffern) + (. Ziffern) + (Ziffern .))
		. 			∈  L(.)
		1_3			∈  L(Ziffern)  =  L((0-9) ((_ + ε) (0-9))*)
			1			∈  L(0-9)
 			_3 			∈  L(((_ + ε) (0-9))*)
				_			∈  L(_ + ε)
				3			∈  L(0-9)
	e-0_2		∈  L(((e + E) (- + ε) Ziffern) + ε)
		e			∈  L(e + E)
		-			∈  L(- + ε)
		0_2			∈  L(Ziffern)  =  L((0-9) ((_ + ε) (0-9))*)
			0			∈  L(0-9)
 			_2 			∈  L(((_ + ε) (0-9))*)
				_			∈  L(_ + ε)
				2			∈  L(0-9)
```

`00_1_2.e2`:
```
00_1_2.e2	∈  L((- + ε) ((Ziffern . Ziffern) + (. Ziffern) + (Ziffern .)) ((- + ε) (e + E) Ziffern) + ε))
	00_1_2.	 	∈  L((Ziffern . Ziffern) + (. Ziffern) + (Ziffern .))
		00_1_2		∈  L(Ziffern)  =  L((0-9) ((_ + ε) (0-9))*)
			0			∈  L(0-9)
			0			∈  L(((_ + ε) (0-9))*)
			_1			∈  L(((_ + ε) (0-9))*)
				_			∈  L(_ + ε)
				1			∈  L(0-9)
 			_2 			∈  L(((_ + ε) (0-9))*)
				_			∈  L(_ + ε)
				2			∈  L(0-9)
		. 			∈  L(.)
	e2			∈  L(((e + E) (- + ε) Ziffern) + ε)
		e			∈  L(e + E)
		2			∈  L(Ziffern)  =  L((0-9) ((_ + ε) (0-9))*)
```



### DFA:

<img src="https://github.com/Cpp-dino/hsbi_compilerbau_portfolio/blob/f057abcd18cf37aac6f6089f5e0e92c12efd558c/images/compiler1_3.png" width="100%">

`-.1_3e-0_2`:

	S  -(-)->  p1  -.->  p2  -1->  p5  -_->  p6  -3->  p5  -e->  p8  -(-)->  p9  -0->  p10  -_->  p11 -2->  p10

`00_1_2.e2`:

	S  -(0)->  p3  -0->  p3  -_->  p4  -1->  p3  -_->  p4  -2->  p3  -.->  p7  -e->  p8  -2->  p10



### Reguläre Grammatik:

```
Start  => (- Abs | (0-9) Int1 | . Float3)
Abs	   => ((0-9) Int1 | . Float3)

Int1	 => ((0-9) Int1 | _ Int2 | . Float3 | . Expo1 | . Ende)
Int2	 => ((0-9) Int1)

Float2 => ((0-9) Float2 | _ Float3 | (0-9) Expo1 | (0-9) Ende)
Float3 => ((0-9) Float2 | (0-9) Expo1 | (0-9) Ende)

Expo1	 => (e Expo2 | E Expo2)
Expo2	 => (- Expo3 | (0-9) Expo4 | (0-9) Ende)
Expo3	 => ((0-9) Expo4 | (0-9) Ende)
Expo4	 => ((0-9) Expo4 | _ Expo3 | (0-9) Ende)

Ende 	 => ε
```

`-.1_3e-0_2`:

                        Start
                         / \
                        -   Abs
                            / \
                          .   Float3
                              / \
                            1   Float2
                                / \
                              _   Float3
                                  / \
                                3   Expo1
                                    / \
                                  e   Expo2
                                      / \
                                    -   Expo3
                                        / \
                                      0   Expo4
                                          / \
                                        _   Expo3
                                            / \
                                          2   Ende
                                               |
                                               ε

`00_1_2.e2`:

                         Start
                          / \
                        0   Int1
                            / \
                          0   Int1
                              / \
                            _   Int2
                                / \
                              1   Int1
                                  / \
                                _   Int2
                                    / \
                                  2   Int1
                                      / \
                                    .   Expo1
                                        / \
                                      e   Expo2
                                          / \
                                        2   Ende
                                             |
                                             ε



## Java:

### Regex:

$`(-\ +\ ε)\ \ ((Ziffern\ .\ Ziffern)\ \ +\ \ (.\ Ziffern)\ \ +\ \ (Ziffern\ .))\ \ (((e\ +\ E)\ (-\ +\ ε)\ Ziffern)\ \ +\ \ ε)\ \ (f\ +\ F\ +\ d\ +\ D\ +\ ε)`$
$`Ziffern\ \ =\ \ (0-9)\ \ ((_)^*\ (0-9^)^*`

`0__1_2.e2d`:
```
-.1__3e-0_2F  ∈  L((- + ε) ((Ziffern . Ziffern) + (. Ziffern) + (Ziffern .)) ((- + ε) (e + E) Ziffern) + ε)) (f + F + d + D + ε)
	- 			∈  L(- + ε)
	.1__3		∈  L((Ziffern . Ziffern) + (. Ziffern) + (Ziffern .))
		. 			∈  L(.)
		1__3		∈  L(Ziffern)  =  L((0-9) ((_)* (0-9))*)
			1			∈  L(0-9)
 			__3 		∈  L((_)* (0-9))*)
				___			∈  L((_)*)
				3			∈  L(0-9)
	e-0_2		∈  L(((e + E) (- + ε) Ziffern) + ε)
		e			∈  L(e + E)
		-			∈  L(- + ε)
		0_2			∈  L(Ziffern)  =  L((0-9) ((_)* (0-9))*)
			0			∈  L(0-9)
 			_2 			∈  L((_)* (0-9))*)
				_			∈  L((_)*)
				2			∈  L(0-9)
	F			∈  L(f + F + d + D + ε)
```

`0__1_2.e2d`:
```
0__1_2.e2d	∈  L((- + ε) ((Ziffern . Ziffern) + (. Ziffern) + (Ziffern .)) ((- + ε) (e + E) Ziffern) + ε)) (f + F + d + D + ε)
	0__1_2.	 	∈  L((Ziffern . Ziffern) + (. Ziffern) + (Ziffern .))
		0__1_2		∈  L(Ziffern)  =  L((0-9) ((_)* (0-9))*)
			0			∈  L(0-9)
			__			∈  L((_)* (0-9))*)
			_1			∈  L(0-9)
				_			∈  L((_)*)
				1			∈  L(0-9)
 			_2 			∈  L(((_)* (0-9) )* )
				_			∈  L((_)*)
				2			∈  L(0-9)
		. 			∈  L(.)
	e2			∈  L(((e + E) (- + ε) Ziffern) + ε)
		e			∈  L(e + E)
		2			∈  L(Ziffern)  =  L((0-9) ((_)* (0-9))*)
	d			∈  L(f + F + d + D + ε)
```



### DFA:

<img src="https://github.com/Cpp-dino/hsbi_compilerbau_portfolio/blob/f057abcd18cf37aac6f6089f5e0e92c12efd558c/images/compiler1_2.png" width="100%">

`-.1__3e-0_2F`:

	S  -(-)->  p1  -.->  p2  -1->  p5  -_->  p6  -_->  p6  -3->  p5
	   -e->  p8  -(-)->  p9  -0->  p10  -_->  p11  -2->  p10  -F->  p12
  
`0__1_2.e2d`:

	S  -0->  p3  -_->  p4  -_->  p4  -1->  p3  -_->  p4  -2->  p3  -.->  p7  -e->  p8  -2->  p10



### Reguläre Grammatik:

```
Start  => ((0-9) Int1 | . Float3 | - Abs)
Abs	   => ((0-9) Int1 | . Float3)

Int1   => ((0-9) Int1 | _ Int2 | . Float3 | . Expo1 | . Ende| . Typ)
Int2	 => ((0-9) Int1 | _ Int2)

Float2 => ((0-9) Float2 | (0-9) Expo1 | (0-9) Ende | (0-9) Typ | _ Float2)
Float3 => ((0-9) Float2 | (0-9) Expo1 | (0-9) Ende | (0-9) Typ)

Expo1	 => (e Expo2 | E Expo2)
Expo2	 => (- Expo3 | (0-9) Expo4 | (0-9) Ende | (0-9) Typ)
Expo3	 => ((0-9) Expo4 | (0-9) Ende | (0-9) Typ)
Expo4	 => ((0-9) Expo4 | (0-9) Ende | (0-9) Typ | _ Expo3)

Typ	   => (f Ende | F Ende | d Ende | D Ende)
Ende 	 => ε
```

`-.1__3e-0_2F`:

                        Start
                         / \
                        -   Abs
                            / \
                          .   Float3
                              / \
                            1   Float2
                                / \
                              _   Float2
                                  / \
                                _   Float2
                                    / \
                                  3   Expo1
                                      / \
                                    e   Expo2
                                        / \
                                      -   Expo3
                                          / \
                                        0   Expo4
                                            / \
                                          _   Expo3
                                              / \
                                            2   Typ
                                                / \
                                              F   Ende
                                                  |
                                                  ε

`0__1_2.e2d`:

                        Start
                         / \
                        0   Int1
                            / \
                          _   Int2
                              / \
                            _   Int2
                                / \
                              1   Int1
                                  / \
                                _   Int2
                                    / \
                                  2   Int1
                                      / \
                                    .   Expo1
                                        / \
                                      e   Expo2
                                          / \
                                        2   Typ
                                            / \
                                          d   Ende
                                               |
                                               ε



# [A1.4: Mailadressen](https://github.com/Compiler-CampusMinden/CB-Vorlesung-Bachelor-W25/blob/master/homework/sheet01.md#a14-mailadressen-1p)

## Probleme beim Regex:

der Regex ist für E-Mail-Addressen ungeignet weil:

- versagt bei Sonderzeichen (zB. +, -, ., etc.) im Lokalen Teil
- versagt bei Ziffern (0-9)
- nur Domains mit 2 Labels möglich (versagt zB. bei 'de.wikipedia.org')
- versagt bei Sub- und Top-Level-Domain mit mehr als 1 Zeichen (Top-Level-Domains sind immer min. 2 Zeichen lang, Regex versagt daher grundsätzlich)
- versagt bei Ziffern oder Bindestrichen in Domain
- verarbeitet nur Kleinbuchstaben, Email-Addressen sind zwar Case-Insensitiv, aber trotzdem können Addressen mit Großbuchstaben verarbeitet werden



## verbessertes Regex:

$`(a-z\ +\ A-Z\ +\ 0-9\ +\ SZ)\ \ ((.\ +\ ε)\ \ (a-z\ +\ A-Z\ +\ 0-9\ +\ SZ))^*\ \ \ \ \ \ \ \ \ \ \ \ \ \ \ \ \ \ \ \ \ \ \ \ \ \ \ \ \ \ \ \ \ \ \ \ \ \ \ \ \ \ \ \ \ \ \ \ \ \ \ \ \ \ \ \ \ \ \ \ \ \ \ (Lokaler Teil)`$
$`((a-z\ +\ A-Z\ +\ 0-9)\ \ ((-\ +\ ε)\ \ (a-z\ +\ A-Z\ +\ 0-9))^*\ .)^+\ \ (a-z\ +\ A-Z)\ \ (a-z\ +\ A-Z)^+\ \ \ \ \ \ \ (Domain)`$

verwendete Aliase:

$`a-z\ \ =\ \ (a\ +\ ...\ +\ z)`$

$`A-Z\ \ =\ \ (A\ +\ ...\ +\ Z)`$

$`0-9\ \ =\ \ (0\ +\ ...\ +\ 9)`$

$`SZ \ \ =\ \ (!\ {+}\ *\ +\ (+)\ +\ -\ +\ /\ +\ =\ +\ ?\ +\ ^\ +\ \_\ +\ '\ +\ \{\ +\ |\ +\ \}\ )`$



# [A1.5: Der zweitletzte Buchstabe](https://github.com/Compiler-CampusMinden/CB-Vorlesung-Bachelor-W25/blob/master/homework/sheet01.md#a15-der-zweitletzte-buchstabe-1p)

<img src="https://github.com/Cpp-dino/hsbi_compilerbau_portfolio/blob/f057abcd18cf37aac6f6089f5e0e92c12efd558c/images/compiler1_4.png" width="100%">



# [A1.6: Sprache einer regulären Grammatik](https://github.com/Compiler-CampusMinden/CB-Vorlesung-Bachelor-W25/blob/master/homework/sheet01.md#a16-sprache-einer-regulären-grammatik-2p)

folgende Sprache wird beschrieben:
- Wort beginnt immer mit `a`
- Wort endet immer mit `da` oder `db`
- dazwischen beliebig oft `b`, `c` oder `da`



# Regex:

$`a\ \ (b\ +\ c\ +\ da)^*\ \ d\ \ (a\ +\ b)`$



# DFA:

<img src="https://github.com/Cpp-dino/hsbi_compilerbau_portfolio/blob/f057abcd18cf37aac6f6089f5e0e92c12efd558c/images/compiler1_5.png" width="100%">


