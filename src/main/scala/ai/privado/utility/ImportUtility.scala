package ai.privado.utility

import scala.util.control.Breaks._
import java.nio.charset.MalformedInputException
import scala.io.Source
import io.shiftleft.codepropertygraph.generated.Languages

object ImportUtility {

  private var allImports: Set[String] = Set();

  // get language specific import matching regex
  private def getMatchImportRegex(language: String): String = {
    val result = language match {
      case Languages.JAVA => "^\\s*(import)\\s+.*$"
      case Languages.PYTHON => "^\\s*(import)\\s+.*$"
      case Languages.JAVASCRIPT => "^\\s*(import|require)\\s+.*$"
      case default => "(import)\\s+" // Default is java
    }
    return result;
  }

  private def getFileExtension(language: String): String = {
    val result = language match {
      case Languages.JAVA => ".java"
      case Languages.PYTHON => ".py"
      case Languages.JAVASCRIPT => ".js"
      case default => ".java" // Default is java
    }

    return result;
  }

  // get regex to match only import words
  private def getLiteralWordRegex(language: String): String = {
    val result = language match {
      case Languages.JAVA => "(import|static)\\s+"
      case Languages.PYTHON => "(import)\\s+"
      case Languages.JAVASCRIPT => "(import|require)\\s+"
      case default => "(import)\\s+" // Default is java
    }
    return result;
  }

  private def returnImportStatements(filePath: String, language: String): Set[String] = {

    val matchImportRegex = getMatchImportRegex(language);
    val onlyLiteralWordRegex = getLiteralWordRegex(language);

    var multilineFlag = false;
    var uniqueImports: Set[String] = Set()
    val result = new StringBuilder("")
    try {
      val source = Source.fromFile(filePath)
      breakable {
        for (line <- source.getLines()) {
          val scanLine = line.trim();
          if (scanLine.matches("/\\*.*")) { // set flag if multiline comment is encountered
            multilineFlag = true;
          }

          // Ignore if inside a multiline comment
          if (!multilineFlag) {
            if (scanLine matches matchImportRegex) {
              val withoutImportLine = scanLine.replace(onlyLiteralWordRegex.r.findAllIn(line).mkString, "");
              uniqueImports += withoutImportLine; // add each import to set
            } else {
              if (!scanLine.matches("(package|//)\\s*.*") && scanLine.lengthCompare(0) != 0) { // Ignore if there is nothing or a package definition on a line
                break
              }
            }
          }

          if (scanLine.matches(".*\\*/")) {
            multilineFlag = false;
          }

        }
      }
    } catch {
      case e: MalformedInputException => println(e)
    } finally {
      source.close()
    }
    return uniqueImports;
  }

  private def scanAllFilesInDirectory(dirpath: String, language: String): Unit = {
    val files = new java.io.File(dirpath).listFiles();
    val fileExtension = getFileExtension(language);

    for (file <- files) {
      if (file.isDirectory())
        scanAllFilesInDirectory(file.getAbsolutePath(), language) // Recursively call for each directory
      else {
        if (file.getAbsolutePath().endsWith(fileExtension)) { // Scan only java files
          var fileImports: Set[String] = returnImportStatements(file.getAbsolutePath(), language);
          for (el <- fileImports) {
            allImports += el;
          }
        }
      }
    }

  }

  def getAllImportsFromDirectory(dirpath: String, language: String): Set[String] = {
    scanAllFilesInDirectory(dirpath, language);
    return allImports;
  }

}
