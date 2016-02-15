#
# Makefile fore sansj
#
include $(BMRBMAKEHEADER)
#
#BMRBBASE=/bmrb
#GLOBALCLASSES=$(BMRBBASE)/javaclasses
#

INSTDIR=$(GLOBALCLASSES)/EDU/bmrb/sansj
FTPDIR=/bmrb/ftp/pub/software/validator
BUILDDIR=build
SRCDIR=src/EDU/bmrb/sansj

JAVAC=javac -target 1.4

all: scanner classes

scanner: src/EDU/bmrb/sansj/star.flex
	cd src/EDU/bmrb/sansj && java JFlex.Main star.flex

classes:
	- mkdir $(BUILDDIR)
	$(JAVAC) -d $(BUILDDIR) $(SRCDIR)/*.java $(SRCDIR)/starlib/*.java

classes-nolib:
	- mkdir $(BUILDDIR)
	$(JAVAC) -d $(BUILDDIR) $(SRCDIR)/starlib/*.java \
	$(SRCDIR)/CifParser.java $(SRCDIR)/ContentHandler.java \
	$(SRCDIR)/DataItemNode.java $(SRCDIR)/ErrorHandler.java \
	$(SRCDIR)/ErrorHandlerNS.java $(SRCDIR)/IntStringPair.java \
	$(SRCDIR)/LoopParser.java $(SRCDIR)/NMRStarParserNS.java \
	$(SRCDIR)/SansParser.java $(SRCDIR)/SimpleStarParser.java \
	$(SRCDIR)/StarContentHandler.java $(SRCDIR)/STARLexer.java \
	$(SRCDIR)/Test.java $(SRCDIR)/TestLoopParser.java \
	$(SRCDIR)/TestStarParser.java

jar:
	jar -cvf dist/sansj.jar $(BUILDDIR)/EDU/bmrb/sansj/*.class \
	$(BUILDDIR)/EDU/bmrb/sansj/starlib/*.class doc/*

install:
	- mkdir -p $(INSTDIR)
	- mkdir $(INSTDIR)/starlib
	cd $(BUILDDIR)/EDU/bmrb/sansj && cp *.class $(INSTDIR)
	cd $(BUILDDIR)/EDU/bmrb/sansj/starlib && cp *.class $(INSTDIR)/starlib
	- cp sansj.jar $(FTPDIR)

javadoc:
	find . -name "*.java" > DOCLIST
	javadoc -d doc/javadoc -doctitle "SANSj documentation" -header "" \
	-windowtitle "SANSj documentation" -breakiterator @DOCLIST
	rm DOCLIST

clean:
	cd src/EDU/bmrb/sansj && rm -f STARLexer.java *\~ *.class

.PHONY: classes clean install

