let mountains = [
    { name: "Monte Falco", height: 1658, place: "Parco Foreste Casentinesi" },
    { name: "Monte Falterona", height: 1654, place: "Parco Foreste Casentinesi" },
    { name: "Poggio Scali", height: 1520, place: "Parco Foreste Casentinesi" },
    { name: "Pratomagno", height: 1592, place: "Parco Foreste Casentinesi" },
    { name: "Monte Amiata", height: 1738, place: "Siena" }
  ];

function generateTableHead(table, data) {
    let thead = table.createTHead();
    let row = thead.insertRow();
    for (let key of data) {
      let th = document.createElement("th");
      let text = document.createTextNode(key);
      th.appendChild(text);
      row.appendChild(th);
    }
  }
  
  function generateTable(table, data) {
    for (let element of data) {
      let row = table.insertRow();
      for (key in element) {
        let cell = row.insertCell();
        let text = document.createTextNode(element[key]);
        cell.appendChild(text);
      }
    }
  }
  
  let table = document.querySelector("table");
  let data = Object.keys(mountains[0]);
  generateTableHead(table, data);
  generateTable(table, mountains);


  function showAllCards() {
    var xhr = new XMLHttpRequest();
    xhr.open('GET', 'http://localhost:8080/list', true);
    xhr.onload = function () {
        if (this.status == 200) {
            let allData = JSON.parse(this.responseText);
            var row = "";
            for (var i = 0; i < allData.length; i++) {
                row += '<div class="col-12 col-md-6 col-lg-3 mb-3"><div class="card">'
                    + '<div class="card-body">'
                    + '<h5 class="card-title" style="border-bottom: 1px solid gray">' + allData[i].name + '</h5>'
                    + '<p class="card-text" onload="minimizeText()" id="words" style="height: 120px; overflow: hidden;">'
                    + allData[i].id
                    + '</p>'
                    + '</div>'

                    + '</div></div>'
                cardRow.innerHTML = row;


            }
            minimizeText();
        }
    }
    xhr.send();

    showAllCards();
};