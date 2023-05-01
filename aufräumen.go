// Ein Program, das den Code deutlich verkürzen kann
// Tests haben gezeigt, dass sich die Zeilenanzahl des Codes um das Tausendfache oder sogar mehr reduzieren kann
// Dass das den Code um einiges lesbarer macht, ist ja wohl klar
package main

import (
	"os"
	"path/filepath"
	"strings"
)

func main() {
	entries, err := os.ReadDir("HerderGames")
	if err != nil {
		panic(err)
	}

	var resultBuilder strings.Builder
	resultBuilder.WriteString("import java.util.*;import processing.core.*;import java.util.stream.*;import java.util.function.*;")

	for _, entry := range entries {
		if entry.IsDir() {
			continue
		}

		if entry.Name() == "HerderGames.pde" {
			continue
		}

		path := filepath.Join("HerderGames", entry.Name())
		content, err := os.ReadFile(path)
		if err != nil {
			panic(err)
		}
		lines := strings.Split(string(content), "\n")
		for _, line := range lines {
			if strings.HasPrefix(line, "import") {
				continue
			}

			if !strings.Contains(line, "http") {
				line = strings.Split(line, "//")[0]
			}

			resultBuilder.WriteString(line)
		}
	}

	replacer := strings.NewReplacer(
		//"    ", " ",
		//"\t", "",
		//"\r", "",
	)

	result := replacer.Replace(resultBuilder.String())
	println(len(strings.Split(result, "\n")))

	err = os.WriteFile("./HerderGames.java", []byte(result), 0644)
	if err != nil {
		panic(err)
	}
}
