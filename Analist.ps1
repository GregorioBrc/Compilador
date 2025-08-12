Write-Host "[1/5] Generando Analizador Lexico con JFlex..."
jflex .\\analizadores\\lexico.flex

Write-Host "[2/5] Generando Analizador Sintactico con JavaCUP..."
java -jar .\\lib\\java-cup-11b.jar .\\analizadores\\sintactico.cup

Write-Host "[3/5] Moviendo Lexico.java a la carpeta src..."
Move-Item -Path .\\analizadores\\Lexico.java -Destination .\\src\\ -Force

Write-Host "[4/5] Moviendo sym.java a la carpeta src..."
Move-Item -Path .\\sym.java -Destination .\\src -Force

Write-Host "[5/5] Moviendo parser.java a la carpeta src..."
Move-Item -Path .\\parser.java -Destination .\\src -Force

Write-Host "Proceso de compilacion completado."
Read-Host "Presiona Enter para salir..."