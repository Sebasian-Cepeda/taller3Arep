movies = (() => {

    function consultMovie() {
        let nameMovie = document.getElementById("movie").value;
        console.log(nameMovie);
        const xhttp = new XMLHttpRequest();
        xhttp.onload = function () {
            document.getElementById("pelicula").innerHTML = this.responseText;
        }
        xhttp.open("GET", "/film?name=" + nameMovie);
        xhttp.send();
    }

    return{ 
        consultMovie
    } 

})();