# STEGOSAURUS - a java image stegonography tool to hide text data.

> Steganography is the art of hiding digital information or text data in other forms of digital data like messages, images, videos, audio files, etc. Image Steganography is the pracice of hiding data into digital images.

Steganosaurus is an image steganograpy tool developed in `Java` and uses `JavaFx` for its GUI. It allows you to hide text data into an image, compress, encrypt, provide a password and a lot more. It works by using the LSB(Least Significant Bit) technique, which involves hiding bits of data into lower bit postions of the RGB color channel, which hold the least significance. Hence any change in them will result in negligible change of color that will be impossible to detect with the naked eye.

#### Features
* Use 24-bit `PNG` and `BMP` images to hide the text data in.
* Compress data using `GZip` compression.
* Encrypt data using the best encryption system around, `AES` (Advanced Encrytion Standard).
* Provide a password that will be used to unlock the data.
* Use at least 1 or all 24 bits of a PNG image.
* View information such as the maximum data that can be uploaded and compressed data size.

#### Information
* Only 24-bit PNG and 24-bit BMP images should be used since they follow a lossless compression, unlike JPG images.
* Must close application after encoding to decode another image and vice-versa.

#### Installation
* Navigate to and run `Stegosaurus/out/artifacts/Stegosaurus_jar/Stegosaurus.jar`

#### Usage




#### Tech
Stegosaurus uses a couple of open source projects to work properly:

* [Java](https://java.com/en/) - HTML enhanced for web apps!
* [JavaFx](http://www.oracle.com/technetwork/java/javase/overview/javafx-overview-2158620.html) - awesome web-based text editor

And of course [Stegosaurus]() itself is open source. Yay! So go ahead and download the source code and feel free to use it wherever you'd like.

#### Requirements

* [Java](https://docs.oracle.com/javase/8/docs/technotes/guides/install/install_overview.html) JDK version `1.8.0_40` or higher.
* [Java](https://docs.oracle.com/javase/8/docs/technotes/guides/install/install_overview.html) JRE version `1.8.0_40` or higher.
* `10` mb of space on hard disk.

#### Todos
 - Basic Steganalysis feature
 - Support for other image formats

#### Credits
* To all the free tutorial channels on Java and JavaFx, especially thenewboston from youtube. You guys are doing a great job!
* To Loyola College, Chennai for giving me this oppurtunity and to Prof. S. Venkatalakshmi, my project guide for helping me out.
[//]: # ()

  
