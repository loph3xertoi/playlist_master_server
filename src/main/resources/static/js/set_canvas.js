// Get a reference to the canvas element
var canvas = document.getElementById('myCanvas');

// Get the width of the viewport
var viewportWidth = window.innerWidth || document.documentElement.clientWidth || document.body.clientWidth;

// Get the height of the viewport
var viewportHeight = window.innerHeight || document.documentElement.clientHeight || document.body.clientHeight;

// Set the canvas width and height to match the viewport size
canvas.width = viewportWidth;
canvas.height = viewportHeight;