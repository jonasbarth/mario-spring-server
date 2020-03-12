filenames = "sources.txt"

file = open(filenames, "r")
write = open("compile2.bat", "w+")

lines = file.readlines()
for line in lines:
    bat_line = "javac " + '"' + line.rstrip() + '"\n'
    write.write(bat_line)




