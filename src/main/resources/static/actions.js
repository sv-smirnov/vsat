const cardRow = document.getElementById("Stations");

console.log(cardRow);


function showAllCards() {
  var xhr = new XMLHttpRequest();
  xhr.open('GET', 'http://localhost:8088/list', true);

  console.log(xhr);

  xhr.onload = function () {
    if (this.status == 200) {
        let allData = JSON.parse(this.responseText);

        // console.log(allData);
        let map = new Map(Object.entries(allData));

        // console.log(`размер мапы ${map.size}`);

        var row = `<table cellspacing="1" border="1" cellpadding="1">` + 
                    `<thead>` +
                      `<tr>` +
                        `<th>id</th>` +
                        `<th>Название станции</th>` +
                        `<th>IP-адрес</th>` +
                        `<th>EbN0</th>` +
                        `<th>Оптореле</th>` +
                        `<th>Status</th>` +
                        `<th>On/Off</th>` +
                      `</tr>`+
                    `</thead>` +
                    `<tbody>`;
        for (let amount of map.values()) {
          // console.log(amount);

          row += `<tr :key="${amount.id}">` +
            `<td><span >${amount.id}</span></td>` +
            `<td><span >${amount.name}</span></td>` +
            `<td><span >${amount.ip}</span></td>` +
            `<td><span >${amount.value}</span></td>` +
            `<td><span >${amount.rele}</span></td>` +
            `<td><span >${amount.status}</span></td>` +
            `<td><button class="btn.${amount.id}" value="Sleep" onclick="toSleep('${amount.id}', '${amount.name}')">Sleep</button></td>` +
            `</tr>`;


        };
        row += `</tbody>` +
                `</table>`;

        cardRow.innerHTML = row;

    }
  }
  xhr.send();

};

function toSleep(id, name) {
  alert(`Передатчик 1MUX ${name} успешно выключен`);

  let formData = new FormData([form]);
  formData.append(`name`, name);
  formData.append(`id`, id);

  var xhr = new XMLHttpRequest();
  xhr.open('POST', `http://localhost:8088/${id}`);

  xhr.send(formData);

  xhr.onload = () => alert(xhr.response);



}


showAllCards();

setInterval(function() {
  showAllCards();
}, 10000);
