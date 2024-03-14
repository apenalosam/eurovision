

function callCities() {
    fetch('http://localhost:1111/api/cities/queryByPage?page=1&size=20', {
        method: "GET",
        headers: {
            'Access-Control-Allow-Origin': 'http://localhost:1111',
            'Access-Control-Allow-Origin': 'http://localhost:63342',
            "Content-Type": "application/json"
        }
    })
        .then(res => {
            return res.json();
        })
        .then(data => {
            var array = data.content
            console.log(array);
            array.forEach(city => {
                const html = `<li>${city.name}</li>`

                document.getElementById('resultList').insertAdjacentHTML('beforeend', html);
            });
        })
        .catch(error => console.error(error));
}