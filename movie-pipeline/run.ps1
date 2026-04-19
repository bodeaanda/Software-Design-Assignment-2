Remove-Item -Recurse -Force target\classes
New-Item -ItemType Directory -Force -Path target\classes
javac -d target\classes (Get-ChildItem -Recurse -Filter "*.java" | Select-Object -ExpandProperty FullName)
Remove-Item -Recurse -Force movie_101
java -cp target\classes com.pipeline.Main "C:\Users\Anda\Desktop\An III\Sem II\SD\Assignment2\sample.mp4" "movie_101"