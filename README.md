# pdf-index
Index pdf files (for every word in PDF document, find the page numbers where it appears).

## Console Application Usage
* `--input` or `-i` - The path to the pdf

Example:
* `IndexCreator -i "C:\Users\Thibaud\Desktop\hello.pdf"`

### Process the output of the main()
* Copy paste the output of the main() in an editor
* Remove the first "{" and the last "}"
* Replace all "], " by carriage return
* Replace all "=[" by a special character ("@" for example)
* Copy paste the result in an Excel sheet:
![excel-image](https://dl.dropboxusercontent.com/u/22987083/pdf-index.png)
