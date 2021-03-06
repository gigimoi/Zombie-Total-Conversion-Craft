#!/bin/bash
mkdir packaging
cp build/libs/* packaging/
cp -rf eclipse/assets/mods/* packaging/
mkdir out
cd packaging
mkdir mods
mv *.jar mods 
rm -rf "1.7.10"
cd ../libs && cp *.jar ../packaging/mods && cd ../packaging
mkdir config
cp -rf ../eclipse/assets/config/* config
tar czf ../out/zombie-total-conversion.tar.gz .