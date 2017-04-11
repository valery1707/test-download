Консольная утилита для скачивания файлов по HTTP протоколу.

[![Build Status](https://travis-ci.org/valery1707/test-download.svg)](https://travis-ci.org/valery1707/test-download)
[![Coverage Status](https://coveralls.io/repos/valery1707/test-download/badge.svg?branch=master&service=github)](https://coveralls.io/github/valery1707/test-download?branch=master)
[![License](https://img.shields.io/github/license/valery1707/test-download.svg)](http://opensource.org/licenses/MIT)

### Входные параметры:

	-n количество одновременно качающих потоков (1,2,3,4....)
	-l общее ограничение на скорость скачивания, для всех потоков, размерность - байт/секунда, можно использовать суффиксы K,M (K=1024, M=1024*1024)
	-f путь к файлу со списком ссылок
	-o имя папки, куда складывать скачанные файлы

### Формат файла со ссылками:

	<HTTP ссылка><пробел><имя файла, под которым его надо сохранить>

пример:

	http://example.com/archive.zip my_archive.zip
	http://example.com/image.jpg picture.jpg
	......

В HTTP ссылке нет пробелов, нет encoded символов и прочей ерунды - это всегда обычные ссылки с английскими символами без специальных символов в именах файлов и прочее. Короче - ссылкам можно не делать decode. Ссылки без авторизации, не HTTPS/FTP - всегда только HTTP-протокол.

Ссылки могут повторяться в файле, но с разными именами для сохранения, например:

	http://example.com/archive.zip first_archive.zip
	http://example.com/archive.zip second_archive.zip

Одинаковые ссылки - это нормальная ситуация, хорошо бы ее учитывать.

### Описание работы

В конце работы утилита должна выводить статистику - время работы и количество скачанных байт.

Утилита написана на Java версии 8.
Для сборки проекта использован gradle.

Пример вызова:

	java -jar download-cli.jar -n 5 -l 2000k -o output_folder -f links.txt

### Сборка

Полная сборка с тестами: `./gradlew build`

Сборка с отчётов покрытия тестами: `./gradlew build jacocoTestReport`

Сам отчёт находится в файле `build/reports/jacoco/test/html/index.html`
