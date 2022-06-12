mkdir -p diggers
cp ../resource/* diggers
javac -d . -cp "../jar/gluegen-rt.jar;../jar/jogl-all.jar" ../src/diggers/*.java
jar cvfm diggers.jar diggers-manifest.mf diggers
