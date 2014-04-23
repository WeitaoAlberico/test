class ReadWriteFile
	attr_accessor :readFile
	attr_accessor :writeFile
	attr_accessor :maxline
	attr_accessor :sampleDir

	def initialize(readFile=nil, writeFile=nil, maxline=nil, sampleDir="")
		@readFile = readFile
		@writeFile = writeFile
		@maxline = maxline

		@sampleDir = sampleDir
		@writeContents = ""
		@readFileNum = 0
	end

	def readFromFile
		@readFileNum += 1
		@title = ""
		@description = ""
		puts "read file " + @readFileNum.to_s
		lineNum = 0
		hasDescription = false
		if @readFile != nil? && @maxline != nil
			puts "start read inside sub-function"
			File.readlines(@readFile).each do |line|
				puts "linenum: " + lineNum.to_s
				if lineNum == 0
					if line[0,1] == "#"
						@title = line.sub("#", "").strip
					else 
						@title = "Wrong title reading. First line of readme file should start with # Title"
					end
					lineNum += 1
				else 
					if lineNum <= @maxline && !hasDescription
						if line.downcase.index("description") >= 0
							hasDescription = true
							@maxline += lineNum
						end
						lineNum +=1
					elsif hasDescription

						if line[0,1] == "#" || line[0,1] == "*" || lineNum > @maxline
							break
						elsif 
							@description +=line
							lineNum +=1
						end
					else
						@description = "Could not find description in readme file."
						break
					end
				end
			end
		end
		@writeContents += "# [" + @title + "][" + @readFileNum.to_s + "] \n\n"
		@writeContents += " " + @description + "\n"
		@writeContents += "[" + @readFileNum.to_s + "]: " + @sampleDir + "\n\n"
				
	end


	def writeToFile
		puts "inside writeToFile function"
		if @writeFile != nil
			File.open(@writeFile, 'w') do |f|
				puts "write string: " + @writeContents
				f.puts @writeContents
			end
		end
	end

end

require 'find'
puts "current dir: " + File.dirname(__FILE__)

# Maximum lines of description can be read from readme.md file.
maxLineOfDescription = 5

writeFile = "./index.md"

rAndwF = ReadWriteFile.new
rAndwF.writeFile = writeFile
rAndwF.maxline = maxLineOfDescription

Find.find('./examples/') do |f|
	if f != "./examples/"
		if File.directory?(f)
			puts "find directory: #{f}"
			puts "loop through #{f}"

			rAndwF.sampleDir = f
			puts "set link of the dir: " + rAndwF.sampleDir

			Find.find(f) do |fi|

				if fi != f
					puts "find file: #{fi}"
					fname = fi[fi.length - 9 .. fi.length]
					puts "file name: " + fname
					i = fname.casecmp "readme.md"

					if i == 0
						rAndwF.readFile = fi
						puts "before read"
						rAndwF.readFromFile
						puts "after read"
						break
					end
				end
			end
		end 
	end
end 

puts "begin write"
rAndwF.writeToFile
puts "after write"

rAndwF = nil


