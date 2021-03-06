# About the application
Simple example login page with 1 Button, 2 EditText and internet request. 
We take the data from the input fields and when you click on the button, send a request to the server and wait for an answer

# Attention! 
This example was created exclusively for very beginner programmers. For such a code you will be dismissed from work but at this stage he will help you understand the essence of the work.

# Preview
![](http://media.giphy.com/media/5b9xDSw5DBiGBGwRak/giphy.gif) ![](http://media.giphy.com/media/4VXZfmSXGJAiC3wsZb/giphy.gif)

# Code
Description of the application code
<details><summary>Open</summary>
<p>

## Manifest
In the [`Manifest`](https://github.com/GssGuru/Login-Beginner/blob/master/app/src/main/AndroidManifest.xml) add only permission on the Internet. Read the comments in the code

## gradle
In the [`gradle`](https://github.com/GssGuru/Login-Beginner/blob/master/app/build.gradle) add only dependencies on the Internet. Read the comments in the code

## Aplication code
[`Aplication code`](https://github.com/GssGuru/Login-Beginner/tree/master/app/src/main/java/guru/gss/loginbeginner) - is the code with the mechanics of the application.
Carefully read the code comments.

Since this project is for beginners, we will write everything in [`LoginActivity`](https://github.com/GssGuru/Login-Beginner/blob/master/app/src/main/java/guru/gss/loginbeginner/LoginActivity.java). Without using any architectural solutions.
- Find and initialize elements in activity
- Validation of data for correctness (email and password) to prevent a request with bad data
- The method that will send a request to the server and wait for a response from the server
- Show request status (Animation on input change and progress)

## Resources code
[`Res folder.`](https://github.com/GssGuru/Login-Beginner/tree/master/app/src/main/res) Change only Application Name

</p>
</details>
