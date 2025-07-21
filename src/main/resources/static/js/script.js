console.log("Script loaded");


// change theme flow started

let currentTheme = getTheme();

document.addEventListener("DOMContentLoaded", () => {
    changeTheme();
});

function changeTheme() {
    //set to web page
    changePageTheme(currentTheme, currentTheme);

    //set listener to change theme button
    const changeThemeButton = document.querySelector('#theme_change_button');

    changeThemeButton.addEventListener("click", (event) => {
        console.log("change theme button clicked");
        let oldTheme = currentTheme;
        if (currentTheme == "dark") {
            currentTheme = "light";
        } else {
            currentTheme = "dark";
        }
        changePageTheme(currentTheme, oldTheme);
    });
}

// set theme to localStorage
function setTheme(theme) {
    localStorage.setItem("theme", theme);
}

// get theme from localStorage
function getTheme() {
    let theme = localStorage.getItem("theme");

    return theme ? theme : "light";
}

// change current page theme
function changePageTheme(theme, oldTheme) {
    //update in localStorage
    setTheme(theme);
    //remove old theme
    document.querySelector('html').classList.remove(oldTheme);
    //add new theme
    document.querySelector('html').classList.add(theme);

    //change button text
    document.querySelector("#theme_change_button").querySelector('span').textContent =
        currentTheme == "light" ? "Dark" : "Light";
}

// change theme flow ended
