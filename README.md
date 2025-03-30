![GitHub Release](https://img.shields.io/github/v/release/birdywood/beact.kt)


![Logo](src/nativeMain/resources/drawable/Logo_Icon.svg?raw=true)
# Beact Markup Language (in Kotlin)

Beact is a compiler for a new language that improves the HTML language. It uses the extension Beact Markup Language (.bml) to give news features to this old language.

## Documentation

[Wiki](https://github.com/BirdyWood/beact.kt/wiki)


## Installation

Installer packages for Windows is found [here](https://github.com/BirdyWood/beact.kt/releases/latest).

> [!NOTE]  
> Since Beact 1.1.0, Java doesn't need to be installed on your computer. The compiler is now written in Kotlin Native and compiled in native code.

## How to use?

To build your project, execute this command

```sh
beact build
```
> [!TIP]
> Install the official extension for Visual Studio Code [here](https://marketplace.visualstudio.com/items?itemName=BirdyWood.beact-plugin)

<!--You can **customize** the build by creating a `./config_beact.json` file

```json
{
  "name": "beact_website",
  "version": "1.0.0",
  "outputDir": "./out",
  "watchDir": "./",
  "excludeDir": [
    "node_modules"
  ]
}
```-->

## Usage/Examples of BML codes

```html
<:BigTitle content="BigTitle_content">
    <h1><#BigTitle_content/> - <#Name/></h1>
    <hr />
</:BigTitle>


<$foreach range="0..10">
    <BigTitle Name="Title"><#times/></BigTitle>
</$foreach>
```


## FAQ

#### Why should I use Beact?

Instead of others librairies to improve frontend developpment, Beact is based on HTML to provide more features to code a website. And it compiles bml files directly in HTML files after making modificatoins, so it's easy to export the files to the production server.

#### How does Beact provide new features while using the HTML structure?

Beact introduce a new type of tag that starts with ```$```. This tag provides access to new functions like
```<$foreach range="0..3"> </$foreach>```


## Feedback

If you have any feedback, please reach out to us at [support@birdywood.fr](mailto:\\support@birdywood.fr)


## License

[MIT](LICENSE.md)
