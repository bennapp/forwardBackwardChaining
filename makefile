JFLAGS = -g
JC = javac
.SUFFIXES: .java .class
.java.class:
	$(JC) $(JFLAGS) $*.java

CLASSES = \
	Variable.java \
	Operator.java \
	And.java \
	Or.java \
	Imp.java \
	Sentence.java \
	Entail.java

default: classes

classes: $(CLASSES:.java=.class)

clean:
	$(RM) *.class