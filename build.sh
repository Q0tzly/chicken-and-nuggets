echo "Cleaning directories..."
rm -rf target dist
mkdir -p target
javac src/*.java -d target
cp -r img/* target
mkdir -p dist
jar vcfm dist/app.jar manifest.txt -C target .
