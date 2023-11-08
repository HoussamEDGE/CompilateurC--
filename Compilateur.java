


////////////////////////////////////////////////////////////////////
                 /// Houssam Elmouqaddam 2GI ///
////////////////////////////////////////////////////////////////////




import java.util.*;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.BufferedReader;
import java.util.LinkedList;




public class Compilateur{
    public static void main(String[] args) {
        ////////////////////////////////////////  lexical >>>>
        try {
            File file = new File("test.txt");
            FileReader reader = new FileReader(file);
            BufferedReader br = new BufferedReader(reader);
            StringBuilder sb = new StringBuilder();
            String line = br.readLine();
            while (line != null) {
                sb.append(line);
                sb.append("\n");
                line = br.readLine();
            }
            br.close();
            System.out.println(sb.toString());
            System.out.println("////////////////////////////////////////////////// lexical");
            String input = sb.toString();
            AnalyseurLexical lexer = new AnalyseurLexical(input);
            lexer.Parseur();
            ArrayList<UniteLexical> tokens = lexer.getUniteLexical();
            for (UniteLexical token : tokens) {
                System.out.println("Type: " + token.getType() + " Value: " + token.getValue());
            }
            
            System.out.println("/////////////////////////////////////////////////////////// vars + type");

            for (int i = 0 ; i < lexer.varptype.capacity ; i++){
                HashMap.HashNode n = lexer.varptype.buckets[i];

                while (n != null) {
                    
                    System.out.println("("+n.getKey() + "," + n.getValue()+")");
                    
                    n = n.getNext();
                }
            }
            System.out.println("/////////////////////////////////////////////////////////// Hashage identifiants");

            for (int i = 0 ; i < lexer.identifiers.capacity ; i++){
                HashMap.HashNode n = lexer.identifiers.buckets[i];

                while (n != null) {
                    
                        System.out.println("value :  "+n.getValue());
                        System.out.println("key :  "+n.getKey());
                    
                    n = n.getNext();
                }
            }

            System.out.println("/////////////////////////////////////////////////////////// Hashage mots clés");

            for (int i = 0 ; i < lexer.MotCles.capacity ; i++){
                HashMap.HashNode n = lexer.MotCles.buckets[i];

                while (n != null) {
                    
                        System.out.println("value :  "+n.getValue());
                        System.out.println("key :  "+n.getKey());
                    
                    n = n.getNext();
                }
            }

            System.out.println("/////////////////////////////////////////////////////////// Syntaxique");

             ///////////////////////////////// syntaxique 

            AnalyseurSynthaxique  synt = new AnalyseurSynthaxique(lexer.MotCles,lexer.identifiers,lexer.ul,lexer.varptype);
            synt.parse();
            System.out.println("/////////////////////////////////////////////////////////// variables");
            for (int i = 0; i < synt.vars.size(); i++) {
                String value = synt.vars.get(i);
                System.out.println(">>> "+value);
            }




        }catch (IOException e) {
            System.out.println("An error occurred while reading from the file.");
            e.printStackTrace();
        }
    }
}

////////////////////////////////// hashage



class HashMap<K, V> {
    public HashNode<K, V>[] buckets;
    private int size;
    public int capacity;


    public HashMap(int capacity) {
        this.capacity = capacity;
        buckets = new HashNode[capacity];
        size = 0;
    }

    public void put(K key, V value) {
        int hash = hashFunction(key);
        HashNode<K, V> node = new HashNode<>(key, value);
        if (buckets[hash] == null) {
            buckets[hash] = node;
        } else {
            HashNode<K, V> current = buckets[hash];
            while (current.getNext() != null && !current.getKey().equals(key)) {
                current = current.getNext();
            }
            if (current.getKey().equals(key)) {
                current.setValue(value);
            } else {
                current.setNext(node);
            }
        }
        size++;
    }

    public V get(K key) {
        int hash = hashFunction(key);
        HashNode<K, V> current = buckets[hash];
        while (current != null) {
            if (current.getKey().equals(key)) {
                return current.getValue();
            }
            current = current.getNext();
        }
        return null;
    }

    public void remove(K key) {
        int hash = hashFunction(key);
        HashNode<K, V> current = buckets[hash];
        HashNode<K, V> prev = null;
        while (current != null) {
            if (current.getKey().equals(key)) {
                if (prev == null) {
                    buckets[hash] = current.getNext();
                } else {
                    prev.setNext(current.getNext());
                }
                size--;
                return;
            }
            prev = current;
            current = current.getNext();
        }
    }

    public boolean containsKey(K key) {
        int hash = hashFunction(key);
        HashNode<K, V> current = buckets[hash];
        while (current != null) {
            if (current.getKey().equals(key)) {
                return true;
            }
            current = current.getNext();
        }
        return false;
    }

    public boolean isEmpty() {
        return size == 0;
    }

    public int size() {
        return size;
    }

    private int hashFunction(K key) {
        if (key instanceof Integer) {
            int intValue = (Integer) key;
            return Math.abs(intValue % capacity);
        } else if (key instanceof String) {
            String strValue = (String) key;
            int hash = 0;
            for (int i = 0; i < strValue.length(); i++) {
                hash = 31 * hash + strValue.charAt(i);
            }
            return Math.abs(hash % capacity);
        } else {
            throw new IllegalArgumentException("type de clé insupportable");
        }
    }


    public static class HashNode<K, V> {
        private K key;
        public V value;
        private HashNode<K, V> next;

        public HashNode(K key, V value) {
            this.key = key;
            this.value = value;
            this.next = null;
        }

        public K getKey() {
            return key;
        }

        public void setKey(K key) {
            this.key = key;
        }

        public V getValue() {
            return value;
        }

        public void setValue(V value) {
            this.value = value;
        }

        public HashNode<K, V> getNext() {
            return next;
        }

        public void setNext(HashNode<K, V> next) {
            this.next = next;
        }
    }
}




//////////////////////////////////////////////////////////////////
 
enum TypeUL {
    BINARY_OPERATOR,
    BINARY_COMPARATOR,
    DOUBLE_AND,
    DOUBLE_OR,
    BRACKET_O,
    BRACKET_F,
    PARENTH_O,
    PARENTH_F,
    NEG,
    COMMA,
    COLON,
    AFFECT,
    CBRACKET_O,
    CBRACKET_F,
    SEMICOLON,
    MOT_CLE,
    CONST_INT,
    CONST_STRING,
    IDENTIF,
    END,
}

class UniteLexical {
    private TypeUL type;
    private Object value;


    public UniteLexical(TypeUL type, Object value) {
        this.type = type;
        this.value = value;

    }

    public TypeUL getType() {
        return type;
    }

    public Object getValue() {
        return value;
    }

}

class AnalyseurLexical{


    public final HashMap MotCles;
 
    //table des operateurs binaires
    private final String binOp[]= {"+","-","<<",">>","*","&","|","/"};
 
    //table des comparateurs binaires
    private final String binComp[]= {"<",">","<=",">=","==","!="};

    public HashMap identifiers ;


    private String input;
    public ArrayList<UniteLexical> ul;
    public HashMap varptype ;

    int kk = 0;
/////////////////////////////////////////////////////////////////////////

    public AnalyseurLexical(){this.MotCles=new HashMap(10);}

    public AnalyseurLexical(String input){
        this.input = input;
        this.ul = new ArrayList<UniteLexical>();
        this.identifiers=new HashMap(50);
        this.MotCles=new HashMap(5);
        this.varptype = new HashMap(5);
        MotCles.put(1,"else");
        MotCles.put(2,"then");
        MotCles.put(3,"for");
        MotCles.put(4,"while");
        MotCles.put(5,"void");
        MotCles.put(6,"int");
        MotCles.put(7,"extern");
        MotCles.put(8,"return");
        MotCles.put(9,"if");
        MotCles.put(10,"chaine");
    }

    public ArrayList<UniteLexical> getUniteLexical() {
        return ul;
    }

    public int getIndexHashmap(String stringacherche, HashMap t){
        int pos = -1;
        for (int i = 0; i < t.capacity; i++) {
            HashMap.HashNode n = t.buckets[i];
            while (n != null) {
                if (n.value.equals(stringacherche)) pos=(int) n.getKey();
                n = n.getNext();
            }
        }
        return pos;
    }



    private int getNombre(int index) {
        StringBuilder number = new StringBuilder();
        while (index < input.length() && Character.isDigit(input.charAt(index))) {
            number.append(input.charAt(index));
            index++;
        }
        return Integer.parseInt(number.toString());
    }

    private String getMot(int index) {
        StringBuilder word = new StringBuilder();
        while (index < input.length() && Character.isLetter(input.charAt(index))) {
            word.append(input.charAt(index));
            index++;
        }
        return word.toString();
    }


    public int getIndex(String stringacherche, String[] stringArray){   ///// retourne l'indice dans un AL
        int post = -1;
        for (int i=0;i<stringArray.length;i++) {
            if (stringArray[i].equals(stringacherche)) {
                post = i;
                return post ;
            }
        }
        return post;
    }






    public void Parseur() {          
        int index = 0;
        int ind = 0;
        int intouchaine = 0;
        String currenttype = "" ; 
        String currentvar;
        while (index < input.length()) {
            char currentChar = input.charAt(index);
            if (currentChar == ';') intouchaine = 0;
            if (Character.isDigit(currentChar)) {       //// si nombre >>>>  CONST_INT 
                int number = getNombre(index);
                ul.add(new UniteLexical(TypeUL.CONST_INT, number));
                index += Integer.toString(number).length();
            } 
            else if (Character.isLetter(currentChar)) {   ///// si lettre >>>> MOT_CLE ou IDENTIFIANT
                String word = getMot(index);
                if (word.equals("int") || word.equals("chaine")){ 
                    intouchaine = 1;
                    currenttype = word ; 
                    System.out.println("currenttype : "+ currenttype);
                }
                int indexWord=getIndexHashmap(word,MotCles);
                if(indexWord!=-1){
                    ul.add(new UniteLexical(TypeUL.MOT_CLE,indexWord ));
                }else if (ind == 0){
                    int indexIdentif=getIndexHashmap(word, identifiers);
                    if(indexIdentif==-1 && intouchaine == 1){
                        kk++;
                        identifiers.put(kk,word); 
                        currentvar = word ;
                        if (input.charAt(index + 1) == '[') currenttype = "tab" ; 
                        System.out.println("currentvar: "+ currentvar);
                        varptype.put(currentvar, currenttype);
                        ul.add(new UniteLexical(TypeUL.IDENTIF,kk ));
                        
                    }else{
                        ul.add(new UniteLexical(TypeUL.IDENTIF,indexIdentif ));
                    }
                }
                index += word.length();
            }
             else if (currentChar == '+') {
                ul.add(new UniteLexical(TypeUL.BINARY_OPERATOR, getIndex("+",binOp)));

                index++;
            }
             else if (currentChar == '-') {
                ul.add(new UniteLexical(TypeUL.BINARY_OPERATOR, getIndex("-",binOp)));
                index++;
            } 
            else if (currentChar == '*') {
                ul.add(new UniteLexical(TypeUL.BINARY_OPERATOR, getIndex("*",binOp)));
                index++;
                if (input.charAt(index) == '/') ind =0;
            } 
            else if (currentChar == '/') {
                ul.add(new UniteLexical(TypeUL.BINARY_OPERATOR, getIndex("/",binOp)));
                index++;
                if (input.charAt(index) == '*') ind =1;
            } 
            else if (currentChar == '(') {
                ul.add(new UniteLexical(TypeUL.PARENTH_O, "("));
                index++;
            } 
            else if (currentChar == ')') {
                ul.add(new UniteLexical(TypeUL.PARENTH_F, ")"));
                index++;
            }
            else if (currentChar == '[') {
                ul.add(new UniteLexical(TypeUL.BRACKET_O, "["));
                index++;
            } 
            else if (currentChar == ']') {
                ul.add(new UniteLexical(TypeUL.BRACKET_F, "]"));
                index++;
            }
            else if (currentChar == ',') {
                ul.add(new UniteLexical(TypeUL.COMMA, ","));
                index++;
            }
            else if (currentChar == ':') {
                ul.add(new UniteLexical(TypeUL.COLON, ":"));
                index++;
            }
            else if (currentChar == '{') {
                ul.add(new UniteLexical(TypeUL.CBRACKET_O, "{"));
                index++;
            } 
            else if (currentChar == '}') {
                ul.add(new UniteLexical(TypeUL.CBRACKET_F, "}"));
                index++;
            }
            else if (currentChar == ';') {
                ul.add(new UniteLexical(TypeUL.SEMICOLON, ";"));
                index++;
            }
            else if(currentChar=='<'){
                index++;
                currentChar = input.charAt(index);
                if(currentChar=='<'){
                    ul.add(new UniteLexical(TypeUL.BINARY_OPERATOR,  getIndex("<<",binOp)));
                    index++;

                }else if(currentChar=='='){
                    ul.add(new UniteLexical(TypeUL.BINARY_COMPARATOR,  getIndex("<=",binComp)));
                    index++;
                }else{
                    ul.add(new UniteLexical(TypeUL.BINARY_COMPARATOR,  getIndex("<",binComp)));

                }
            }
            else if(currentChar=='>'){
                index++;
                currentChar = input.charAt(index);
                if(currentChar=='>'){
                    ul.add(new UniteLexical(TypeUL.BINARY_OPERATOR,  getIndex(">>",binOp)));
                    index++;

                }else if(currentChar=='='){
                    ul.add(new UniteLexical(TypeUL.BINARY_COMPARATOR,  getIndex(">=",binComp)));
                    index++;
                }else{
                    ul.add(new UniteLexical(TypeUL.BINARY_COMPARATOR,  getIndex(">",binComp)));
                }
            }
            else if(currentChar=='='){
                index++;
                currentChar = input.charAt(index);
                if(currentChar=='='){
                    ul.add(new UniteLexical(TypeUL.BINARY_OPERATOR,  getIndex("==",binComp)));
                    index++;
                }else{
                    ul.add(new UniteLexical(TypeUL.AFFECT,"="));
                }
            }

            else if(currentChar=='&'){
                index++;
                currentChar = input.charAt(index);
                if(currentChar=='&'){
                    ul.add(new UniteLexical(TypeUL.DOUBLE_AND, "&&"));
                    index++;

                }else {
                    ul.add(new UniteLexical(TypeUL.BINARY_COMPARATOR, getIndex("&",binOp)));
                }
            }

            else if(currentChar=='|'){
                index++;
                currentChar = input.charAt(index);
                if(currentChar=='|'){
                    ul.add(new UniteLexical(TypeUL.DOUBLE_OR, "||"));
                    index++;

                }else {
                    ul.add(new UniteLexical(TypeUL.BINARY_COMPARATOR, getIndex("|",binOp)));
                }
            }

            else if(currentChar=='!'){
                index++;
                currentChar = input.charAt(index);
                if(currentChar=='='){
                    ul.add(new UniteLexical(TypeUL.BINARY_COMPARATOR, getIndex("!=",binOp)));
                    index++;

                }else {
                    ul.add(new UniteLexical(TypeUL.NEG, "!"));
                }
            }

            else if(currentChar=='"'){
                index++;
                currentChar = input.charAt(index);
                String chain="";
                while(currentChar!='"'){
                    currentChar = input.charAt(index);
                    if(currentChar!='"')chain+=currentChar;
                    index++;

                }
                ul.add(new UniteLexical(TypeUL.CONST_STRING, chain));
                index++;

            }
            
            else{
                index++;
            }
        }
        ul.add(new UniteLexical(TypeUL.END,0));
    }
}




//////////////////////////////////////////////////////////////////////////////
//////////////////////////////////////////////////////////////////////////////
//////////////////////////////////////////////////////////////////////////////


class AnalyseurSynthaxique {

    private final HashMap MotCles;

    // table des operateurs binaires
    private final String binOp[] = { "+", "-", "<<", ">>", "*", "&", "|", "/" };

    // table des comparateurs binaires
    private final String binComp[] = { "<", ">", "<=", ">=", "==", "!=" };

    // tables des identifiers
    private HashMap identifiers ;
    private ArrayList<UniteLexical> ul;
    private int currentIndex=0;

    // sémantique
    public ArrayList<String> vars = new ArrayList<String>() ;
    public String currentvar = "" ;
    public HashMap varptype ;

    public void addVart(){
        if (!vars.contains((String)identifiers.get(currentToken().getValue()))) 
            vars.add((String)identifiers.get(currentToken().getValue()));
    }

    public void Syntaxerr(){
        if (identifiers.get(currentToken().getValue()) == null) throw new RuntimeException("Variable not declared !");
    }
    //////////

    public AnalyseurSynthaxique() {
        this.MotCles = new HashMap(5);
        this.identifiers = new HashMap(50);
        this.varptype = new HashMap(5);
        this.ul = new ArrayList<UniteLexical>();
    }

    public AnalyseurSynthaxique(HashMap MOTCLE,HashMap identifs , ArrayList<UniteLexical> UL , HashMap VP) {
        this.MotCles = MOTCLE;
        this.ul = UL;
        this.identifiers = identifs;
        this.varptype = VP ;
    }

    public boolean isThisKeyW(String st){
        for (int i=0 ; i<MotCles.capacity ; i++){
            HashMap.HashNode n = MotCles.buckets[i];
            while(n != null){
                if (currentToken().getValue() == n.getKey()) return ((String)n.getValue() == st); 
                n = n.getNext();
            }
        }
        return false;
    }
 
    public void parse() {
        // start with the <Programme> rule
        parseProgramme();
        if (currentIndex != ul.size() - 1) {
            throw new RuntimeException("Unexpected tokens at the end of input");
        }
        System.out.println(">>>> Syntax correcte !");
    }

    private boolean match(TypeUL expected) {
        if (currentToken().getType() == expected) {
            currentIndex++;
            return true;
        } else {
            return false;
        }
    }

    private UniteLexical currentToken() {
        return ul.get(currentIndex);
    }



    private boolean typeCurrentToken(TypeUL expected){
        return  currentToken().getType() == expected;
    }
    private boolean valueCurrentToken(int v){
        int t =  (int)currentToken().getValue() ;
        return t == v ;
    }

    private void parseComment(){
        if (typeCurrentToken(TypeUL.BINARY_OPERATOR) && valueCurrentToken(7)){
            match(TypeUL.BINARY_OPERATOR);
            if (typeCurrentToken(TypeUL.BINARY_OPERATOR) && valueCurrentToken(4)){
                    match(TypeUL.BINARY_OPERATOR);
                    System.out.println("comment started");
                    while(!(typeCurrentToken(TypeUL.BINARY_OPERATOR) && valueCurrentToken(4))) currentIndex++;
                    if (typeCurrentToken(TypeUL.BINARY_OPERATOR) && valueCurrentToken(4)){
                        match(TypeUL.BINARY_OPERATOR);
                        if (typeCurrentToken(TypeUL.BINARY_OPERATOR) && valueCurrentToken(7)){
                            match(TypeUL.BINARY_OPERATOR);
                            System.out.println("comment finished");
                            parseProgramme();
                        }else{
                            throw new RuntimeException("Erreur commentaire!!! Un slash est expecte a ce niveau, mais vous avez: " + currentToken().getType());
                        }
                    }
            }
        }
    }


    ////////////////////////// 1
        private void parseProgramme() {
            System.out.println("Régle 1");
            parseComment();
            parseListDeclarations();
            parseListeFonctions();
        }

        ////////////////////////// 2
        private void parseListDeclarations() {
            System.out.println("Régle 2");
            if (isThisKeyW("int") || isThisKeyW("chaine")) {
                match(TypeUL.MOT_CLE);
                parseDeclaration();

            } else {

            }
        }

        ////////////////////////// 3
        private void parseDeclaration() {
            System.out.println("Régle 3");
            if (typeCurrentToken(TypeUL.IDENTIF)) {
                System.out.println("parsedeclaration : "+identifiers.get(currentToken().getValue()));
                parseListeDeclarateurs();
                if (match(TypeUL.SEMICOLON)){
                    parseListDeclarations();
                } else if (typeCurrentToken(TypeUL.PARENTH_O)) {
                    parseFonction();
                    parseListeFonctions();
                } else {
                    throw new RuntimeException("Erreur dans la régle 3!!! Un point virgulle ou une parenthese ouvrante sont expectes a ce niveau, mais vous avez: " + currentToken().getType());

                }

            } else {
                throw new RuntimeException("Erreur dans la régle 3!!! La regle de production <declaration> n'est bien definie a ce niveau, mais vous avez: " + currentToken().getType());
                
            }
        }

        ////////////////////////// 4
        private void parseListeDeclarateurs() {
            System.out.println("Régle 4");
            parseDeclarateur();
            parseListeDeclarateursPrime();
        }

        ////////////////////////// 5
        private void parseListeDeclarateursPrime() {
            System.out.println("Régle 5");
            if (match(TypeUL.COMMA)) {
                parseDeclarateur();
                parseListeDeclarateursPrime();
            } else {
                // epsilon
            }
        }

        ////////////////////////// 6
        private void parseDeclarateur() {
            System.out.println("Régle 6");
            System.out.println("parsedeclarateur : "+identifiers.get(currentToken().getValue()));
            System.out.println(vars);
            if (vars.contains(identifiers.get(currentToken().getValue()))) 
            throw new RuntimeException("Double declaration de la variable : "+identifiers.get(currentToken().getValue())+" !");
            addVart();
            if (match(TypeUL.IDENTIF)) {
                parseDeclarateurPrime();
            } else {
                throw new RuntimeException("Erreur dans la régle 6!!! Un identificateur est expexte a ce niveau, mais vous avez: " + currentToken().getType());

            }
        }

        ////////////////////////// 7
        private void parseDeclarateurPrime() {
            System.out.println("Régle 7");
            if (match(TypeUL.BRACKET_O)) {
                if (match(TypeUL.CONST_INT)) {
                    if (match(TypeUL.BRACKET_F)) {
                    } else
                    throw new RuntimeException("Erreur dans la regle 7!!! Une bracket fermante est expectee a ce niveau, mais vous avez: " + currentToken().getType());


                } else
                throw new RuntimeException("Erreur dans la regle 7!!! Une constante de type 'int' est expectee a ce niveau, mais vous avez: " + currentToken().getType());

            } else {
                //////////// epsilon
            }
        }

        ////////////////////////// 8
        private void parseListeFonctions() {
            System.out.println("Régle 8");
            if (isThisKeyW("int") || isThisKeyW("chaine")) {
                match(TypeUL.MOT_CLE);
                System.out.println("parseListeFonctions : "+identifiers.get(currentToken().getValue()));
                if (match(TypeUL.IDENTIF)) {
                    parseFonction();
                    parseListeFonctions();
                } else {
                    throw new RuntimeException("Erreur dans la regle 8!!! Un identificateur est expectee a ce niveau, mais vous avez: " + currentToken().getType());

                }
            } else if (isThisKeyW("void") || isThisKeyW("extern")) {
                parseListeFonctionsPrime();
            }else if(typeCurrentToken(TypeUL.END)){ 
            } else {
                throw new RuntimeException("Erreur dans la regle 8!!! La regle <liste-fonctions> n'est bien definie a ce niveau. Vous avez: " + currentToken().getType());


            }
        }

        ////////////////////////// 9
        private void parseListeFonctionsPrime() {
            System.out.println(" Régle 9");
            if (isThisKeyW("void")) {
                match(TypeUL.MOT_CLE);
                System.out.println("parseListeFonctionsPrime : "+identifiers.get(currentToken().getValue()));
                if (match(TypeUL.IDENTIF)) {
                    parseFonction();
                    parseListeFonctions();
                } else {
                    throw new RuntimeException("Erreur dans la regle 9!!! Un identificateur est expexte a ce niveau, mais vous avez: " + currentToken().getType()); 

                }
            } else if (isThisKeyW("extern")) {
                parseExtern();
                parseListeFonctions();
            } else {
                //// epsilon
            }
        }

        ////////////////////////// 10

        private void parseExtern() {
            System.out.println(" Régle 10");
            if (isThisKeyW("extern")) {
                match(TypeUL.MOT_CLE);
                parseType();
                System.out.println("parseExtern : "+identifiers.get(currentToken().getValue()));
                if (match(TypeUL.IDENTIF)) {
                    if (match(TypeUL.PARENTH_O)) {
                        parseListeParms();
                        if (match(TypeUL.PARENTH_F)) {
                            if (match(TypeUL.SEMICOLON)) {
                            } else {
                                throw new RuntimeException("Erreur dans le regle 10!!! Un point virgulle est expecte a ce niveau, mais vous avez: " + currentToken().getType());

                            }
                        } else {
                            throw new RuntimeException("Erreur dans la regle 10!!! Une parenthese fermante est expectee a ce niveau, mais vous avez: " + currentToken().getType());

                        }
                    } else {
                        throw new RuntimeException("Erreur dans la regle 10!!! Une parenthese ouvrante est expectee a ce niveau, mais vous avez: " + currentToken().getType());

                    }
                } else {
                    throw new RuntimeException("Erreur dans la regle 10!!! Un identificateur est expecte a ce niveau, mais vous avez:" + currentToken().getType());

                }
            }
        }

        ////////////////////////// 11
        private void parseFonction() {
            System.out.println("Regle 11 ");
            if (match(TypeUL.PARENTH_O)) {
                parseListeParms();
                if (match(TypeUL.PARENTH_F)) {
                    if (match(TypeUL.CBRACKET_O)) {
                        parseListDeclarations();
                        parseListeInstructions();
                        if (match(TypeUL.CBRACKET_F)) {} 
                        else {
                            throw new RuntimeException("Erreur dans la regle 11!!! Une bracket fermante est expectee a ce niveau, mais vous avez:" + currentToken().getType());
                        }
                    } 
                    else {
                        throw new RuntimeException("Erreur dans la regle 11!!! Une bracket ouvrante est expectee a ce niveau, mais vous avez: " + currentToken().getType());
                    }
                } 
                else {
                    throw new RuntimeException("Erreur dans la regle 11!!! Une bracket ouvrante est expectee a ce niveau, mais vous avez: " + currentToken().getType());
                }
            } 
            else {
                throw new RuntimeException("Erreur dans la regle 11!!! Une parenthese ouvrante est expextee a ce niveau, mais vous avez: " + currentToken().getType());
            }
        }

        ////////////////////////// 12
        private void parseType() {
            System.out.println("Regle 12: <type>");
            if (isThisKeyW("int") || isThisKeyW("chaine") || isThisKeyW("void")) {
                match(TypeUL.MOT_CLE);
            } 
            else {
                throw new RuntimeException("Erreur dans la regle 12!!! Un type 'int', 'chaine' ou 'void' est expexte a ce niveau, mais vous avez: " + currentToken().getType());
            }
        }

        ////////////////////////// 13
        private void parseListeParms() {
            System.out.println("Regle 13 ");
            parseListeParmsPrime();
        }

        ////////////////////////// 14
    private void parseListeParmsPrime() {
            //// System.out.println(" c");
            System.out.println("Regle 14");
            parseParm();
            if (match(TypeUL.COMMA)) {
                parseListeParmsPrime();
            } else {
                // epsilon
            }
        }

        ////////////////////////// 15
        private void parseParm() {
            System.out.println("Regle 15");
            if (isThisKeyW("int") || isThisKeyW("chaine")) {
                match(TypeUL.MOT_CLE);
                System.out.println("parseparm : "+identifiers.get(currentToken().getValue()));
                if (match(TypeUL.IDENTIF)) {

                } 
                else {
                    throw new RuntimeException("Erreur dans la regle 15!!! Un identificateur est expecte a ce niveau, mais vous avez: " + currentToken().getType());
                }
            } 
            else {
                ///// epsilon    
            }
        }

        ////////////////////////// 16
        private void parseListeInstructions() {
            System.out.println("Régle 16");
            parseListeInstructionsPrime();
        }

        ////////////////////////// 17
        private void parseListeInstructionsPrime() {
            System.out.println("Régle 17");
            if (typeCurrentToken(TypeUL.CBRACKET_O) || typeCurrentToken(TypeUL.IDENTIF) || isThisKeyW("for")
                    || isThisKeyW("while") || isThisKeyW("if") || isThisKeyW("return")) {
                parseInstruction();
                parseListeInstructionsPrime();
            } else {
                //////// epsilon
            }
        }

        ////////////////////////// 18
        private void parseInstruction() {
            System.out.println("Régle 18");
            if (typeCurrentToken(TypeUL.IDENTIF)) {
                Syntaxerr();
                currentvar = (String)identifiers.get(currentToken().getValue()) ;
                System.out.println(">>>>>>>>>>>>>>>>>>>>>>>>>> currentvar is : "+currentvar + " , it's type is : "+varptype.get(currentvar));
                System.out.println("parseinstruction / currentvar : "+currentvar);
                match(TypeUL.IDENTIF);
                if ( (varptype.get(currentvar).equals("tab")) && (typeCurrentToken(TypeUL.BRACKET_O) == false) )
                throw new RuntimeException(currentvar+" est un tableau !");
                parseInstructionPrime();
            } else if (isThisKeyW("for") || isThisKeyW("while")) {
                parseIteration();
            } 
            else if (isThisKeyW("if")) {
                parseSelection();
            } 
            else if (isThisKeyW("return")) {
                parseSaut();
            } 
            else if (typeCurrentToken(TypeUL.CBRACKET_O)) {
                parseBloc();
            } 
            else {
                throw new RuntimeException("Erreur dans la regle 18!!! La regle de production <instruction> n'est pas bien conçue a ce niveau. Vous devez avoir soient 'if', 'while', 'for' ou 'return' mais vous avez: " + currentToken().getType());
            }
        }

        ////////////////////////// 19
        private void parseInstructionPrime() {
            System.out.println("Regle 19");
            if (match(TypeUL.PARENTH_O)) {
                parseListeExpressions();
                if (match(TypeUL.PARENTH_F)) {
                    if (match(TypeUL.SEMICOLON)) {} 
                    else {
                        throw new RuntimeException("Erreur dans la regle 19 !!! Un point virgule est expecte a ce niveau, mais vous avez: " + currentToken().getType());
                    }
                } 
                else {
                    throw new RuntimeException("Erreur dans la regle 19!!! Une parenthese fermante est expectee a ce niveau, mais vous avez:" + currentToken().getType());
                }
            }
            else if (typeCurrentToken(TypeUL.BRACKET_O) ||  typeCurrentToken(TypeUL.AFFECT)) {
                parseVariablePrime();
                if (match(TypeUL.AFFECT)) {
                    if (typeCurrentToken(TypeUL.CONST_INT)) {
                        System.out.println(" >>>>>>>>>>>>>> int : "+currentToken().getValue());
                        if ( (!(varptype.get(currentvar).equals("int")) && !(varptype.get(currentvar).equals("tab"))) )
                        throw new RuntimeException("type incompatible pour la variable : "+currentvar+" , il faut qu'il soit int");
                    }
                    if (typeCurrentToken(TypeUL.CONST_STRING)) {
                        System.out.println(" >>>>>>>>>>>>>> chaine : "+currentToken().getValue());
                        if ( (!(varptype.get(currentvar).equals("chaine"))) )
                        throw new RuntimeException("type incompatible pour la variable : "+currentvar+" , il faut qu'il soit chaine");
                        match(TypeUL.CONST_STRING);
                    } 
                    else if (typeCurrentToken(TypeUL.PARENTH_O) || typeCurrentToken(TypeUL.CONST_INT)
                            || typeCurrentToken(TypeUL.IDENTIF) || typeCurrentToken(TypeUL.BINARY_OPERATOR)) {
                        parseExpression();
                        if (match(TypeUL.SEMICOLON)) {} 
                        else {
                            throw new RuntimeException("Erreur dans la regle 19 !!! Un point virgule est expecte a ce niveau, mais vous avez: " + currentToken().getType());
                        }
                    } 
                    else {
                        throw new RuntimeException("Erreur dans la regle 19 !!! Une parenthese ouvrante ou un identificateur ou une constante de type int ou un operateur binaire sont expectes a ce niveau, mais vous avez: " + currentToken().getType()); 
                    }
                    
                } 
                else {
                    throw new RuntimeException("Erreur dans la regle 19 !!! Une affectation est expectee a ce niveau, mais vous avez: " + currentToken().getType());
                }

            } 
            else {
                throw new RuntimeException("Erreur dans la regle 19!!! Une bracket ouvrante ou une parenthese ouvrante sont expectees a ce niveau. Vous avez mal conçue la regle de prodution <instruction'> mais vous avez: " + currentToken().getType());
            }
        }

        ////////////////////////// 20
        private void parseVariable() {
            System.out.println("Régle 20");
            System.out.println("parsevariable : "+identifiers.get(currentToken().getValue()));
            Syntaxerr();
            if (match(TypeUL.IDENTIF)) {
                parseVariablePrime();
            } else {
                throw new RuntimeException(
                        "Erreur dans la regle 20!!! Un identificateur est expecte a ce niveau, mais vous avez: "
                                + currentToken().getType());
            }
        }

        ////////////////////////// 21
        private void parseVariablePrime() {
            System.out.println("Régle 21");
            if (match(TypeUL.BRACKET_O)) {
                parseExpression();
                if (match(TypeUL.BRACKET_F)) {
                } else {
                    throw new RuntimeException("Erreur dans la regle  21!!! Une bracket fermante est expectee a ce niveau, mais vous avez: " + currentToken().getType());

                }
            } else {
                // epsilon
            }
        }

        ////////////////////////// 22
        private void parseIteration() {
            System.out.println("Régle 22");
            if (isThisKeyW("for")) {
                match(TypeUL.MOT_CLE);
                if (match(TypeUL.PARENTH_O)) {
                    parseAffectation();
                    if (match(TypeUL.SEMICOLON)) {
                        parseCondition();
                        if (match(TypeUL.SEMICOLON)) {
                            parseAffectation();
                            if (match(TypeUL.PARENTH_F)) {
                                parseInstruction();
                            } else {
                                throw new RuntimeException("Erreur dans la regle 22!!! Une parenthese fermante est expectee a ce niveau, mais vous avez: " + currentToken().getType());
                            }
                        } 
                        else {
                            throw new RuntimeException("Erreur dans la regle 22 !!! Un point virgule est expecte a ce niveau, mais vous avez: " + currentToken().getType());
                        }
                    } 
                    else {
                        throw new RuntimeException("Erreur dans la regle 22!!! Un point virgule est expecte a ce niveau, mais vous avez: " + currentToken().getType());
                    }
                } 
                else {
                    throw new RuntimeException("Erreur dans la regle 22 !!! Une parenthese ouvrante est expecte a ce niveau, mais vous avez: " + currentToken().getType());
                }
            } else if (isThisKeyW("while")) {
                match(TypeUL.MOT_CLE);
                if (match(TypeUL.PARENTH_O)) {
                    parseCondition();
                    if (match(TypeUL.PARENTH_F)) {
                        parseInstruction();
                    } else {
                        throw new RuntimeException("Erreur dans la regle 22!!! Une parenthese fermante est expectee a ce niveau, mais vous avez: " + currentToken().getType());
                    }
                } 
                else {
                    throw new RuntimeException("Erreur dans la regle 22!!! Une parenthese ouvrante est expectee a ce niveau, mais vous avez: " + currentToken().getType());
                }
            } 
            else {
                throw new RuntimeException("Erreur dans la regle 22 !!! Une boucle 'for' ou 'while' sont expectees a ce niveau. Vous avez mal conçue la regle de production <iteration> mais vous avez: " + currentToken().getType());
            }
        }

        ////////////////////////// 23
        private void parseSelection() {
            System.out.println("Régle 23");
            if (isThisKeyW("if")) {
                match(TypeUL.MOT_CLE);
                if (match(TypeUL.PARENTH_O)) {
                    parseCondition();
                    if (match(TypeUL.PARENTH_F)) {
                        parseInstruction();
                        parseSelectionPrime();
                    } else {
                        throw new RuntimeException("Erreur dans la regle 23 !!! Une parenthse fermante est expectee a ce niveau, mais vous avez: " + currentToken().getType());
                    }
                } 
                else {
                    throw new RuntimeException("Erreur dans la regle 23 !!! Une parenthse ouvrante est expectee a ce niveau, mais vous avez: " + currentToken().getType());
                }
            }
            else{
                throw new RuntimeException("Erreur dans la regle 23 !!! Une boucle 'if' est expectee a ce niveau. Vous avez mal conçue la regle de production <selection> mais vous avez: " + currentToken().getType());
            }
        }

        ////////////////////////// 24
        private void parseSelectionPrime() {
            System.out.println("Régle 24");
            if (isThisKeyW("else")) {
                match(TypeUL.MOT_CLE);
                parseInstruction();
            } else {
                ////////// epsilon
            }
        }

        ////////////////////////// 25
        private void parseSaut() {
            System.out.println("Régle 25");
            if (isThisKeyW("return")) {
                match(TypeUL.MOT_CLE);
                parseSautPrime();
            } else {
                throw new RuntimeException("Erreur dans la regle 25 !!! Un 'return' est expecte au niveau de la regle de production <saut>, mais vous avez: " + currentToken().getType());

            }
        }

        ////////////////////////// 26
        private void parseSautPrime() {
            System.out.println("Régle 26");
            if (match(TypeUL.SEMICOLON)) {

            } else if (typeCurrentToken(TypeUL.IDENTIF) || typeCurrentToken(TypeUL.CONST_INT)
                    || typeCurrentToken(TypeUL.PARENTH_O) || typeCurrentToken(TypeUL.BINARY_OPERATOR)) {
                parseExpression();
                if (match(TypeUL.SEMICOLON)) {
                } else {
                    throw new RuntimeException("Erreur dans la regle 26 !!! Un identificateur est expecte a ce niveau, mais vous avez: " + currentToken().getType());
                }
            } 
            else {
                throw new RuntimeException("Erreur dans la regle 26 !!! Un point virgule ou un identificateur sont expectes a ce niveau. Vous avez mal conçue la regle de production <saut'> mais vous avez: " + currentToken().getType());
            }
        }

        ////////////////////////// 27
        private void parseAffectation() {
            System.out.println("Régle 27");
            parseVariable();
            if (match(TypeUL.AFFECT)) {
                parseExpression();
            } else {
                throw new RuntimeException(" Erreur dans la regle 27 !!! Une affectation est expectee a ce niveau, mais vous avez: " + currentToken().getType());

            }
        }

        ////////////////////////// 28
        private void parseBloc() {
            System.out.println("Régle 28");
            if (match(TypeUL.CBRACKET_O)) {
                parseListeInstructions();
                if (match(TypeUL.CBRACKET_F)) {
                } else {
                    throw new RuntimeException("Erreur dans la regle 28!!! Une crochet fermante est expectee a ce niveau, mais vous avez: " + currentToken().getType());
                }
            } 
            else {
                throw new RuntimeException(" Erreur dans la regle 28!!! Une crochet ouvrante est expectee a ce niveau. Vous avez mal conçue la regle de production <bloc> mais vous avez: " + currentToken().getType());
            }

        }

        ////////////////////////// 30
        private void parseExpression() {
            System.out.println("Régle 30");
            if (match(TypeUL.PARENTH_O)) {
                parseExpression();
                if (match(TypeUL.PARENTH_F)) {
                    parseExpressionPrime();
                } else {
                    throw new RuntimeException("Erreur dans la regle 30!!! Une parenthese fermante est expectee a ce niveau, mais vous avez: " + currentToken().getType());

                }
            } else if (match(TypeUL.BINARY_OPERATOR)) {
                parseExpression();
                parseExpressionPrime();
            } else if (typeCurrentToken(TypeUL.IDENTIF)) {
                System.out.println("parseExpression : "+identifiers.get(currentToken().getValue()));
                Syntaxerr();
                match(TypeUL.IDENTIF);
                parseExpressionDoublePrime();
            } else if (match(TypeUL.CONST_INT)) {
                parseExpressionPrime();
            } else {
                throw new RuntimeException("Erreur dans la regle 30!!! Une parenthese ouvrante ou un operateur binaire ou un identificateur ou une constante de type 'int' sont expectes a ce niveau. Vous avez mal concu la regle de production <expression> mais vous avez: " + currentToken().getType());

            }
        }

        ////////////////////////// 31
        private void parseExpressionPrime() {
            System.out.println("Régle 31");
            if (match(TypeUL.BINARY_OPERATOR)) {
                parseExpression();
                parseExpressionPrime();
            } else {
                ///// : epsilon
            }
        }

        ////////////////////////// 32
        private void parseExpressionDoublePrime() {
            System.out.println("Régle 32");
            if (typeCurrentToken(TypeUL.BRACKET_O) || typeCurrentToken(TypeUL.BINARY_OPERATOR)) {
                parseVariablePrime();
                parseExpressionPrime();
            }else if (match(TypeUL.PARENTH_O)) {
                parseListeExpressions();
                if (match(TypeUL.PARENTH_F)) {
                    parseExpressionPrime();
                } else {
                    throw new RuntimeException("Erreur dans la regle 32!!! Une parenthese fermante est expectee a ce niveau, mais vous avez: " + currentToken().getType());

                }
            }else if (typeCurrentToken(TypeUL.BINARY_COMPARATOR)){ ///
            }else if (typeCurrentToken(TypeUL.BRACKET_F)){
            }else if (typeCurrentToken(TypeUL.SEMICOLON)){
            }else{
                throw new RuntimeException("Erreur dans la regle 32!!! Une bracket fermante est expectee a ce niveau, mais vous avez: " + currentToken().getType());

            }
        }

        ////////////////////////// 33
        private void parseListeExpressions() {
            System.out.println("Régle 33");
            if (typeCurrentToken(TypeUL.IDENTIF) || typeCurrentToken(TypeUL.CONST_INT)
                    || typeCurrentToken(TypeUL.PARENTH_O) || typeCurrentToken(TypeUL.BINARY_OPERATOR)) {
                parseExpression();
                parseListeExpressionsPrime();
            }
        }

        ////////////////////////// 34
        private void parseListeExpressionsPrime() {
            System.out.println("Régle 34");
            if (match(TypeUL.COMMA)) {
                parseExpression();
                parseListeExpressionsPrime();
            } else {
                ////////// epsilon
            }
        }

        ////////////////////////// 35 : <chaine>

        ////////////////////////// 36
        private void parseCondition() {
            boolean condExp = false;
            int index2 = currentIndex;
            System.out.println("Régle 36");
            if (match(TypeUL.NEG)) {
                if (match(TypeUL.PARENTH_O)) {
                    parseCondition();
                    if (match(TypeUL.PARENTH_F)) {
                        parseConditionPrime();
                    } else {
                        throw new RuntimeException("Erreur dans la regle 36!!! Une parenthese fermante est expectee a ce niveau, mais vous avez: " + currentToken().getType());

                    }
                } else {
                    throw new RuntimeException("Erreur dans la regle 36!!! Une parenthese ouvrante est expectee a ce niveau, mais vous avez: " + currentToken().getType());

                }
            } else if (match(TypeUL.PARENTH_O)) {
                parseCondition();
                if (match(TypeUL.PARENTH_F)) {
                    parseConditionPrime();
                } else {
                    condExp = true;
                }
            }
            if (condExp == true || typeCurrentToken(TypeUL.IDENTIF) || typeCurrentToken(TypeUL.BINARY_OPERATOR)
                    || typeCurrentToken(TypeUL.CONST_INT)) {
                currentIndex = index2;
                parseExpression();
                if (match(TypeUL.BINARY_COMPARATOR)) {
                    parseExpression();
                    parseConditionPrime();
                } else {
                    throw new RuntimeException("Erreur dans la regle 36!!! Un operateur de comparaison est expecte a ce niveau, mais vous avez: " + currentToken().getType());

                }
            }
        }

        ////////////////////////// 37
        private void parseConditionPrime() {
            System.out.println("Régle 37");
            if (match(TypeUL.DOUBLE_AND) || match(TypeUL.DOUBLE_OR)) {
                parseCondition();
                parseConditionPrime();
            } else {
                // epsilon
            }
        }
    }