Introduction 

	Le but de ce projet est de réaliser un petit compilateur d’un sous-ensemble du langage C appelé C- - vers un langage intermédiaire lui aussi sous-ensemble de C appelé intC avec quelques optimisations.
	Dans la phase de l’analyse lexicale et syntaxique, on cherche à regrouper l’ensemble des caractères isolés constituant le texte source afin de former des unités lexicales représentant les mots du langage et à indiquer si un texte est grammaticalement correct.
Ce rapport présente le travail demandé à réaliser pour établir ces deux parties. Il va détailler les différentes phases parcourues pour élaborer l’alphabet, la liste des unités lexicales, le diagramme de transition, l’implémentation de l’analyseur lexical en JAVA ainsi que la structure du programme sous forme de l’arbre syntaxique.

 


![image](https://github.com/HoussamEDGE/CompilateurC--/assets/99811097/b74e615e-545d-4942-bdd5-48a592b108e6)



I.	Analyse Lexicale
    
1.	L’alphabet du langage :
∑  =  {0 , 1 , 2 , 3 , 4 , 5 , 6 , 7 , 8 , 9, a , b , c , d , e , f , g , h , i , j , k , l , m , n , o , p , q , r , s , t , u , v ,w , x , y , z, A , B , C , D , E , F , G , H , I , J , K , L , M , N ,O , P , Q , R , S , T , U , V , W , X , Y , Z, +, /, *, -, >, <, |, &, =, !, ,, ), (, [, ], }, {, ;}

2.	La liste des unités syntaxiques :

enum TUnite {
BINARY_OPERATOR,
BINARY_COMPARATOR,
    	DOUBLE_AND,
    	DOUBLE_OR,
   	CROCHET_OUV,
    	CROCHET_FER,
    	PARENTH_OUV,
    	PARENTH_FER,
    	NEG,
    	VIRG,
    	AFFECT, 
    	ACCOLADE_OUV,
    	ACCOLADE_FER, 
    	POINT_VIRG,
    	MOT_CLE,
    	CONST,
    	IDENTIF ; 
} 
3.	La structure des données :

 ![image](https://github.com/HoussamEDGE/CompilateurC--/assets/99811097/4b219271-e28d-4ed6-98e4-fdd4f4f452d9)

![image](https://github.com/HoussamEDGE/CompilateurC--/assets/99811097/45358214-be6b-4bed-b1b1-db4585ff4ee3)

 


4.	La grammaire améliorée du langage C--:

<Programme> 		: < liste-declarations> < liste-fonctions>

<liste-declarations> 	: <liste-declarations> < declaration> | epsilon

< liste-fonctions> 		: <liste-fonctions> <fonction> |  epsilon 

<declaration>		: (int|chaine) <liste-declarateurs> ; 

<liste-declarateurs>	            : <liste-declarateurs> , <declarateur> |<declarateur>

<declarateur> 	: identificateur | identificateur [constante ] 

<fonction> 	: <type> identificateur ( <liste-parms> ) { <liste-declarations> <liste-instructions>}  |  extern <type> identificateur ( <liste-parms> ) ; 

<type>			: void I int | chaine

<liste-parms> 		: <liste-parms> , <parm> | epsilon 

<parm> 			: int identificateur ,chaine identificateur

<liste-instructions>        	: <liste-instructions>< instructions > | epsilon 

<instruction> 		: < iteration> I <selection>  | <saut> | <affectation>   ; | <bloc> I <appel> 

<iteration> 	: for ( < affectation> ; <condition> ; <affectation> ) <instruction> |      while ( <condition> ) <instruction> 

<selection>	: if ( <condition> ) <instruction> |  if ( <condition> ) <instruction> else <instruction> 

<saut> 	: return ; | return <expression> ; 

<affecation>  : <variable> = (<expression>|<chaine>)
 
<bloc> 	: { <liste-instructions> }
<appel>	: identificateur ( <liste-expressions> ) ; 

<variable> 	: identificateur | identificateur[<expression>] 

<expression> : ( <expression> ) | <expression> <binary-op> <expression>|-   <expression> | <variable> |constante| identificateur (<liste-expressions>)

<liste-expressions>	: <liste-expressions>,<expression> | epsilon

<condition>	: !(<condition>) | <condition><binary-rel><condition> | (<condition >)|<expression><binary-comp><expression>

<chaine>	: “(<letter>|<chiffre>)*”

<binary-op>	:+|-|*|/|<<|>>|&|||

<binary-rel>	: && | ||

<binary-comp>	:<|>|>=|<=|==| !=

<chiffre>                    : 0|1|2|3|4|5|6|7|8|9	

<lettre>      : a|b|c|d|e|f|g|h|i|j|k|l|m|n|o|p|q|r|s|t|u|v|w|x|y| z|A|B|C|D|E|F|G|H|I|J|K|L|M|N|O|P|Q|R|S|T|U|V|W|X|Y|Z

5.	La liste des unités lexicales :


Unité lexicale	Lexème	Attribut	Modèle
BINARY_OPERATOR


	Ex:  
“+”  , ”/” …	Indice dans la table des opérateurs binaires	
+|/|*|-|>>|<<|||&
BINARY_COMPARATOR	Ex:
“<” , ”>”, “==” ...	Indice dans la table des opérateurs de comparaisons	
<|>|>=|<=|==|!=
DOUBLE_AND	“&&”	-	&&
DOUBLE_OR	“||”	-	||
CROCHET_OUV
	“[“	-	[
CROCHET_FER
	“]”	-	]
PARENTH_OUV 
	“(“	-	“(“
PARTH_FER
	‘’)’’	-	‘’)’’
NEG
	‘’ ! ’’	-	!
COLON	‘’ : ’’	-	:
COMMA
	‘’,’’	 -	      ,
AFFECT	‘’=’’	  -	      =
CBRACKET_O
	‘’{‘’	      X	        {
CBRACKET_F
	‘’}’’	      X	        }
SEMICOLON
	‘’;‘’	      X	        ;
MOT_CLE


	Ex : “return”
Ex :  “int”….	Indice dans la table des mots clés	Else | then| for | while | void| int | extern | return | if
CONST_INT
	Ex :  “15”	La valeur de l’entier	      (0|1|… | 9)*
CONST_STRING	Ex :  “houssam”	La valeur du string 	(a|b|… | z)*
IDENTIF	EX : “variable1”	L’indice dans la table des identificateurs	lettre(lettre|chiffre)*






6.	Les tables composant des unités lexicales :

i.	La table des mots clés :

Mot Clé	Lexème	Indice 
INT	“int”	1
VOID	“void”	2
EXTERN	“extern”	3
RETURN	“return”	4
IF	“if”	5
THEN		‘’then’’	6
ELSE	“else”	7
FOR   	‘’for’’	8
WHILE	         	‘’while’’	9
CHAINE	‘’chaine’’	10


ii.	La table des opérateurs binaires :

Opérateur binaire	Lexème	Indice
PLUS	‘’+””	0
MOINS	“-“	1
DIV	“/”	2
MULTIP	“*”	3
AND	“&”	4
OR	“|”	5
DOUBLE_INF	“<<”	6
DOUBLE_SUP	“>>”	7


iii.	La table des opérateurs de comparaison :

Opérateurs de comparaison	Lexème	Indice
INF	“<”	0
SUP	“>”	1
INF_EQUAL	“<=”	2
SUP_EQUAL	“>=”	3
EQUAL	“==”	4
DIFF	“!=”	5

7.	Le diagramme de transition (diagramme d’états) :

 
![image](https://github.com/HoussamEDGE/CompilateurC--/assets/99811097/e4915f2e-3bfd-4e2a-b2ac-8d4cc8ed76e4)

 
 ![image](https://github.com/HoussamEDGE/CompilateurC--/assets/99811097/c5ef2d7a-a81f-4d91-81f5-d5d80ac21f67)

 ![image](https://github.com/HoussamEDGE/CompilateurC--/assets/99811097/42c533f7-2b5c-42fd-8040-044efe367676)

 ![image](https://github.com/HoussamEDGE/CompilateurC--/assets/99811097/b4dce506-6c6c-44c7-ae8e-91c31a57aab7)

![image](https://github.com/HoussamEDGE/CompilateurC--/assets/99811097/f28ee01c-6f69-48ea-afcd-f535054292b2)


II.	Analyse Syntaxique

1.	La version finale de la grammaire améliorée :


R1) <programme> : < liste-declarations>< liste-fonctions> 
R2) <liste-declarations> : int <declaration> |chaine <declaration> |epsilon
R3) <declaration> : <liste-declarateurs> ; <liste-declaration >| <fonction> <liste-fonctions>
R4) <liste-declarateurs> : <declarateur> <liste-declarateurs’>
R5) <liste-declarateurs’> : , <declarateur> <liste-declarateurs’> | epsilon
R6) <declarateur> : identificateur <declarateur’>
R7) <declarateur’> : [constante] | epsilon
R8) <liste-fonctions> : int identificateur <fonction> <liste-fonctions> | chaine identificateur <fonction> <liste-fonctions> | <liste-fonctions’> 
R9) <liste-fonctions’> : void identificateur <fonction> <liste-fonctions> |<expression1> <liste-fonctions> 
R10) <expression1 > : extern <type> identificateur ( <liste-params> ) ;
R11) <fonction> : ( <liste-params> ) { <liste-declarations> < liste-instructions>}
R12) <type> : void I int | chaine
R13) <liste-params> : <liste-params’> 
R14) <liste-params’> :  <parm>, <liste-params’> | epsilon
R15) < parm> : int identificateur | chaine identificateur
R16) <liste-instructions> : <liste-instructions'>
R17) <liste-instructions'> : < instruction ><liste-instructions’>| epsilon
R18) <instruction> :  identificateur<instruction’>| <iteration> I <selection> | <saut> | <bloc>
R19) <instruction’> : ( <liste-expressions> ) ;   | <variable’> = <expression> ; |<variable’> =CONST_STRING ;
R20) <variable> : identificateur <variable’>
R21) <variable’> : [<expression>]  | epsilon
R22) <iteration> : for ( < affectation> ; <condition> ; <affectation> ) <instruction> | while( <condition> )   <instruction>
R23) <selection> :if ( <condition> ) <instruction><selection’>  
R24) <selection’> :else<instruction> | epsilon 
R25)  <saut> :return <saut’>
R26)  <saut’> : ; |<expression>;
R27)  <affectation> : <variable> = <expression>
R28) <bloc> : { <liste-expressions>}
R29) <appel> : identificateur ( <liste-expressions> ) ;
R30) <expression> : ( <expression> ) <expression’> | -<expression><expression’>  | identificateur <expression’’> | <constante> <expression’>
R31) <expression’> : <binary-op> <expression><expression’> | epsilon 
R32) <expression’’> : <variable’> <expression’> | (<liste-expressions>) <expression’> | epsilon
R33) <liste-expressions> : <expression><liste-expressions’>
R34) <liste-expressions’> : , <expression> <liste-expressions’>| epsilon
R35) <chaine> : « (lettre | chiffre)* »
R36) <condition> : !(<condition>) <condition’> | (<condition >) <condition’> ¬| 
<expression><binary-comp><expression><condition’>  
R37) <condition’> : <condition><binary-rel><condition’> | epsilon
R38) <chiffre> : 0|1|2|3|4|5|6|7|8|9	
R39) <letter> : a|b|c|d|e|f|g|h|i|j|k|l|m|n|o|p|q|r|s|t|u|v|w|x|y|z| A|B|C|D|E|F|G|H|I|J|K|L|M|N|O|P|Q|R|S|T|U|V|W|X|Y|Z

1.	Justifications et Explications de 

Règle 1 : <programme> : < liste-declarations>< liste-fonctions> 
La règle 1 est LL(1) puisqu’elle ne présente qu’un seul choix.

Règle 2 : <liste-declaration> : (int |chaine) <declaration> |epsilon
On a PREMIER( int<declaration>) ∩ PREMIER( chaine<declaration>) ∩ { epsilon } = ∅ .
D’autre part, int <declaration> et chaine<declaration> sont non annulables avec PREMIER (< liste-declarations>) = {int,chaine, epsilon} et SUIVANT(< liste-declarations>) = {void, extern}
d’où PREMIER (< liste-declarations>) ∩ SUIVANT(< liste-declarations>) = ∅.
Alors la règle 2 est LL(1)

Règle 3 :<declaration> : <liste-declarateurs> ; <liste-declaration >| <fonction> <liste-fonctions>
On a :
PREMIER (<liste-declarateurs>) = { ‘[‘ , ‘,’ }                                                              PREMIER (<fonction>)= { ‘(‘ } 
et donc PREMIER (<liste-declarateurs>) ∩ PREMIER (<fonction>)=∅.
Et comme les deux sont non annulables alors le règle 3 est LL(1).

Règle 4 : <liste-declarateurs> : <declarateur> <liste-declarateurs’>
La règle est LL(1) puisqu’on a un seul choix

Règle 5 : <liste-declarateurs’>: , <declarateur> <liste-declarateurs’> | epsilon
On a l’intersection des premiers des deux sous règles de <liste-declarateurs’> est l’ensemble vide ; et comme une seule est non annulable, on a :
PREMIER (<liste-declarateurs’>) = {‘,’, epsilon} et 
SUIVANT (<liste-declarateurs’>) = {void, extern} d’où l’intersection est l’ensemble vide.
Alors la règle 5 est LL(1)

Règle 6 : <declarateur> : identificateur <declarateur’>
La règle 6 est bonne puisqu’on a seulement un seul choix


Règle 7 : <declarateur’> : [constante] | epsilon
PREMIER ([constante]) = { [ } et  PREMIER ( epsilon) = { epsilon }
PREMIER (<declarateur’>) = { [ ,epsilon } et 
SUIVANT (<declarateur’>) ={identificateur }
Donc, la règle est LL(1)


Règle 8 : <liste-fonctions> : int identificateur <fonction> <liste-fonctions> | chaine identificateur <fonction> <liste-fonctions> | <liste-fonctions’> 
PREMIER (<liste-fonctions’>) = {void, extern} l’intersection avec {int} et {epsilon} est l’ensemble vide.

PREMIER (<liste-fonctions>) = {int, chaine, void, extern} et SUIVANT (<liste-fonctions>) = {$}, donc l’intersection est vide ce qui signifie que la règle8 est LL(1) 
 

Règle 9 : <liste-fonctions’> : void identificateur <fonction> <liste-fonctions> |<expression1> <liste-fonctions> 
PREMIER (<liste-fonctions’>) = {void, extern } l’intersection avec {int} et {epsilon} est l’ensemble vide.
Et comme les deux sont non annulables alors la règle 9 est LL(1)


Règle 10 : <expression1 > : extern <type> identificateur ( <liste-params> ) ;
La règle 10 est LL(1) puisqu’on a seulement un seul choix.

Règle 11 : <fonction> : ( <liste-params> ) { <liste-declarations> < liste-instructions>}
La règle 11 est LL(1) puisqu’on a un seul choix

 
Règle 12 : <type> : void I int | chaine

La règle 12 est LL(1) puisqu’elle représente trois choix différents non annulables.
Règle 13 : <liste-params> : <liste-params’> 
La règle 13 est LL(1) puisqu’on a seulement un seul choix

 Règle 14 : <liste-params’> : , <parm> <liste-params’> | epsilon 
PREMIER (<liste-params’>) = { ,} et SUIVANT (<liste-params’>)= { ) } 
L’intersection de premier et de suivant est vide. Donc la règle est LL(1)

Règle 15 : < parm> : int identificateur | chaine identificateur
La règle 15 est LL(1) puisqu’elle représente 2 choix différents non annulables.
Règle 16: <liste-instructions> : <liste-instructions'>
La règle 16 est LL(1) puisqu’on a un seul choix

Règle 17: <liste-instructions'> : < instruction ><liste-instructions’>| epsilon
PREMIER (< instructions ><liste-instructions’>)=  {for,while,if,return,{,identificateur}
 et PREMIER(epsilon  ) = { epsilon  } sont disjoints.

La deuxième production est annulable (epsilon) et : 
PREMIER (<liste-instructions’>)={for,while,if,return,{,identificateur, epsilon}
et SUIVANT (<liste-instructions’>)={ } }sont disjoints. 
D’où la régle 17 est LL(1)

Règle 18: <instruction> :  identificateur<instruction’>| <iteration> I <selection> | <saut> | <bloc>

Les premiers des productions sont respectivement : {identificateur}  , {for,while} , {if} , {return} et { { } sont tous disjoints
Toutes les productions sont non annulables. La règle est LL(1)

Règle 19: <instruction’> : ( <liste-expressions> ) ;   | <variable’> = <expression> | <variable’> = CONST_STRING ;
Les premiers respectifs sont { ( } et {epsilon, [ } ils sont disjoint.
Les deux productions sont non annulables. 
Donc, la règle 19 est LL(1)

Règle 20: <variable> : identificateur <variable’>
Cette règle est LL(1) puisqu’on a seulement un seul choix

Règle 21: <variable’> : [<expression>]  | epsilon
Les premiers respectifs sont { [} et {epsilon } .Ils sont disjoint.
PREMIER (<variable’>)= { [, epsilon} et  SUIVANT (<variable’>)= {=}
Donc l’intersection est vide. D’où, la règle est LL(1)

Règle 22:<iteration> : for ( < affectation> ; <condition> ; <affectation> ) <instruction> | while( <condition> )   <instruction>
Les premiers successifs sont {for} et {while}, ils sont disjoints. Les deux productions sont non annulables.                                                           Donc, la règle est LL(1)

Règle 23: <selection> :if ( <condition> ) <instruction><selection’>  
La règle est LL(1) puisqu’on a un seul choix

Règle 24: <selection’> :else<instruction> | epsilon 
Les premiers successifs sont {else} et {epsilon} sont disjoints.
PREMIER (<selection’>)={else,epsilon}
 et SUIVANT (<selection’>)={ } } sont disjoints. 
Donc, la règle est LL(1)

Règle 25: <saut> :return <saut’>
La règle est LL(1) puisqu’on a seulement un seul choix

Regle26: <saut’> : ; |<expression>;

Les premiers successifs sont {;} et { ( , - , identidicateur,  0,1,..,9} sont disjoints
Aucune des productions n'est annulable 

Règle 27: <affectation> : <variable> = <expression>
La règle est LL(1) puisqu’on a un seulement seul choix.

Règle 28:  <bloc> : { <liste-expressions>}
La règle est LL(1) puisqu’on a un seulement seul choix.

Regle29: <appel> : identificateur ( <liste-expressions> ) ;
La règle est LL(1) puisqu’on a un seul choix

Regle30: <expression> : ( <expression> ) <expression’> | -<expression><expression’>  | identificateur <expression’’> | <constante> <expression’>
Tous les choix sont non annulables avec des premiers disjoints.      On a PREMIER (<expression>) = { (; -; identificateur, CONST_INT}
Donc, cette règle est LL( 1)

Regle31: <expression’> : <binary-op> <expression><expression’> | epsilon  
On a un membre annulable : epsilon
On a PREMIER (<expression’>) = { <binary-op>,epsiolon }
Alors que SUIVANT (<expression’> )= { ; , ] }
Les deux sont disjoints. D’où, la règle est LL(1)

Règle 32: <expression’’> : <variable’> <expression’> | (<liste-expressions>) <expression’>
Les premiers des deux membres disjoints et ils sont non annulables tel que PREMIER (<variable’>)= {identificateur} et PREMIER ((<liste-expressions’>))= {( }.
Donc, cette règle est LL( 1)

Règle 33: <liste-expressions> : <expression><liste-expressions’>
La règle est LL(1) puisqu’on a seulement un seul choix


Règle 34: <liste-expressions’> : , <expression> <liste-expressions’>| epsilon 
La règle est LL(1) car on a un membre annulable.
et PREMIER (<liste-expressions’> ) = {, , epsilon}
SUIVANT (<liste-expressions’> )={ <binary-op>,)}

Règle 35: <chaine> : « (lettre | chiffre)* »
La règle est LL(1) car elle représente 2 choix non annulables avec des premiers disjoints


Règle 36: <condition> : !(<condition>) <condition’> | (<condition >) <condition’> ¬| 
<expression><binary-comp><expression><condition’>  
Cette règle conteint une ambiguité qui est très difficile à éliminer ou à supprimer puisque on a PREMIER (<condition>)={!, ( , premier de expression} et PREMIER (<expression >)={(; -; identificateur ; CONST_INT}
D’où , on a opté à donner un ordre de priorité pour les expressions devant les conditions.


Règle 37: <condition’> : <condition><binary-rel><condition’> | epsilon
On a un membre annulable, alors :
PREMIER (<condition’>)={!, ( , identificateur , constante}
SUIVANT(<condition’>)={<binary-rel>,),;}
Donc, les deux sont disjoints . D’où, la règle est LL(1)

Règle 38: <chiffre> : 0|1|2|3|4|5|6|7|8|9
	
Règle 39:<letter> : a|b|c|d|e|f|g|h|i|j|k|l|m|n|o|p|q|r|s|t|u|v|w|x|y|z| A|B|C|D|E|F|G|H|I|J|K|L|M|N|O|P|Q|R|S|T|U|V|W|X|Y|Z


III.	CODAGE

1-Hashage :
On crée la class Hashmap dont  elle prend une clé et une valeur ( K et V) , ici K et V sont des types géneraux , afin de pouvoir donner le type qu’on veut pour la clé et la valeur et ne pas se focaliser sur un type précis .

 

Comme ca se voit dans la figure ci-dessous notre hashmap se compose d’un tableau et dans chaque case dont l’indice est le résultat de hashfunction(clé)  se compose d’une liste chainée qui se compose de hashnodes appelé buckets .
Size c’est le nombres de nœuds , capacity c’est le nombres de cases .
 
 

La classe Hashnode définit un nœud , ces atributs sont la clé , la valeur et le nœud suivant .

 
Pour la fonction put , elle permet de stocker (clé , valeur) dans le hashmap .
Pour les collisions , cad la hashfunction retourne une valeur déjà trouvé (ne pointe pas sur null) , on insère un nouveau nœud dans la meme case dont on pointe sur le nouveau nœud et ce nœud pointe sur le nœud précedent .
 

 

La fonction get retourne la valeur correspondante à la clé en parcourant tous les nœuds dans la case correspondante . 
 
La fonction remove permet de supprimer le nœud correspandant à une clé 
 
La fonction containskey nous renseigne si la clé se trouve dans un nœud dans le hashmap

 
 

  Pour la hashfunction elle supporte 2 type pour la clé (Integer et String) , si entier elle retourne le reste de la clé divisé par la capacité , si String elle calcule 31*0+31*1+….+31*n tq n est la taille du mot et puis elle retourne le  reste de cette valeur divisé par la capacité .

 Pour Les mots clés et les identifiants (table de symbole) on utilise notre hashmap pour les stockés , on les déclares commes  attributs dans l’analyseur lexical et l’analyseur syntaxique : 
Exp : 
 
 

 
 
Après dans le main , on parcours le hashmap :
 
 
Cela nous donne :
 

 
2-Lexical :
  Dans le Lexical en essaye de définir les unités lexicales (Tokens) et l’analyseur lexicale qui va parcourir le ficher est donne le type de l’unité lexical et sa valeur que sa soit un identifiant ou un mot cle et sa cle dans le hashmap ou Arraylist .
On définit les types des unités lexical (extrait de la grammaire) en utilisant enum 
 
La class UniteLexical définit l’unité lexical en prennant son type et sa valeur  
 
  La class AnalyseurLexical est la class qui définit l’analyseur lexical , dont on définit le tableau des opérateurs binaires, tableau des comparateurs binaire, un arraylist des identifiants un buffereader pour le fichier, un char qui nous donne le symbole ou on est arrivé dans la lecture du fichier et finalement un arraylist qui stoque les unités lexicales .
 

Les constructeurs : 

 

Dont on initialise la table des unités lexicales et le hashmap des mots clés ,et ajoute les mots clés et leurs clés dans le hashmap 
La méthode getIndexHasmap permet de retourner la position de la valeur donné si trouver dans le hashmap sinon -1
 
 La méthode getnombre nous retourne le nombre en parcourant chiffre par chiffre .
 
La méthode getMot nous retourne le mot en parcourant lettre par lettre .
 
La méthode getIndex nous retourne l’indice du string dans la table.
 
La méthode parseur est la fonction principale de la class AnalyseurLexical (analyseur lexical) , dont on lit tout le fichier et on extrait les différents unités lexicals 
 
 
 

Remarque : dans la version précedente (avec le groupe) il y’avait des problèmes au niveau des Identifiants dont le Hashmap (on peut le considère comme table de symbole) prend des valeurs erronés , donc j’ai distingué q’il faut prendre que les mots après int ou chaine et avans semicolon « ; » , aussi j’ai ajouté une fin pour les unités lexicales car cela causait des problèmes par la suite dans le syntaxique


 



3-Syntaxique:

La structure des données :

L’analyse syntaxique est l’une des opérations majeures d’un compilateur qui consiste à indiquer si un texte est grammaticalement correct et à en tirer une représentation interne.
Dans le cas du projet demandé à réaliser, on a adopté le code suivant :
 
	J’ ai défini l’ensemble des variables qu’on va utiliser pour réaliser notre analyseur syntaxique. 
	Maintenant après que la grammaire est LL (1), on a les outils pour construire un analyseur récursif descendant à la main. On va faire une traduction fidèle de notre grammaire de telle sorte qu’on va écrire l’ensemble des procédures mutuellement récursive, pour chaque non-terminal de la grammaire. On peut ajuster le type de retour et les paramètres formelles de la fonction. 
	On lance notre analyseur syntaxique en appelant la procédure associée au symbole de départ de notre grammaire <programme>
 
Après on continue de la meme manière que la dernière fois .
Ce que j’ai ajouté :

Pour savoir si c’est un mot clé ou non .
 
J’ai parsecomment qui lit les commentaires au début :
 
 
>>> Après il y a des erreurs dans le suivi de la grammaire , dont je l’ai corriger , vous pouvez voir le code.


4-Sémantique:

L'analyse sémantique est le processus de compréhension du sens d'un texte. Dans le traitement automatique des langues naturelles, l'analyse sémantique consiste à extraire les informations sémantiques d'un texte, c'est-à-dire à comprendre le sens des mots, des phrases et des textes complets. Cela peut inclure l'identification de relations sémantiques entre les mots, la reconnaissance de concepts et d'entités nommées, et la génération de résumés automatiques. L'analyse sémantique est un élément clé de nombreuses applications de NLP telles que la compréhension de la langue naturelle, la génération de réponses automatiques, et la catégorisation de textes.
L'analyse sémantique est un domaine de recherche actif en traitement automatique des langues naturelles, et de nombreux algorithmes et outils ont été développés pour la réaliser. Certains de ces outils utilisent des méthodes statistiques pour identifier les relations sémantiques dans un texte, tandis que d'autres utilisent des méthodes symboliques basées sur des ontologies et des graphes de concepts.

Il existe également des ressources linguistiques telles que des lexiques et des ontologies qui peuvent être utilisées pour l'analyse sémantique, permettant de mieux comprendre les relations entre les mots et les concepts.

Enfin, l'analyse sémantique est un élément clé de la compréhension de la langue naturelle, la dernière étape pour comprendre les requêtes d'un utilisateur ou pour répondre à ses demandes. Elle est également utilisée dans de nombreux domaines tels que la recherche d'informations, la génération de contenu automatique, la traduction automatique et le traitement des sentiments.

Pour moi c’était pas ma conception pour la sémantique , donc je l’ai refait :


La non déclaration d’une variable :

 
La méthode Syntaxerr() permet de faire ce travail dont elle voit si la variable se trouve dans le Hashmap des identifiants sinon elle donne une erreur , après il suffit de localiser ou passe les variables dans la grammaire pour ajouter cette fonction et faire le test : 
 
 
 

Exp : 
 
 








La double déclaration d’une variable :

 
J’ai créer l’arraylist vars qui va stocker que les variables déclarés en utilisant addVart() .
Tous ce passe dans la méthode parseDeclarateur() ou on fait le test .
 
Exp : 
 


 


le contrôle de type entre déclaration et utilisation d’une variable :

Pour cela j’ai retourné au lexical pour récolter les couples (variable,type) car c’est plus facile, et pour cela j’ai utilisé le Hashmap varptype :
 

 
J’ai ajouté currenttype pour récolter le type et l’insérer dans varptype avec les varibles qui se définissent après :
 
 
On passe à la phase syntaxique et on a hashmap varptype qui contient tout les couples (var , type) :
 
Maintenant il faut bien distinguer les différents types (chaine , int , tab) des variables donc il faut localiser ou faire le test dans la grammaire .
On peut faire cela au niveau de parseInstruction() et parseInstructionPrime() :

 
 
 
Le test c’est une comparaison du type de la variable courante avec le type dans le hashmap varptype pour la meme variable si c’est le meme c’est bon , sinon une erreur est surlevé .





Exp 1 : 
 
 







Exp 2 : 
 
 
Exp 3 :
 
 

