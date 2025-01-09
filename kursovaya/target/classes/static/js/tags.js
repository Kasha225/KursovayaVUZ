
    document.addEventListener("DOMContentLoaded", function () {
    const tagsInput = document.getElementById("tags-input");
    const tagsDropdown = document.getElementById("tags-dropdown");

    // Функция для загрузки тегов из базы данных
    function loadTags() {
    fetch('/tags') // GET-запрос на сервер
    .then(response => response.json())
    .then(tags => {
    tagsDropdown.innerHTML = ''; // Очищаем список
    tags.forEach(tag => {
    const tagItem = document.createElement("div");
    tagItem.textContent = tag.name;
    tagItem.dataset.tagName = tag.name;
    tagsDropdown.appendChild(tagItem);

    // Добавляем обработчик клика для выбора тега
    tagItem.addEventListener("click", function () {
    tagsInput.value = this.dataset.tagName; // Устанавливаем значение в поле
    tagsDropdown.classList.add("hidden"); // Закрываем выпадающий список
});
});
})
    .catch(error => console.error("Ошибка при загрузке тегов:", error));
}

    // Открытие списка при нажатии на поле ввода
    tagsInput.addEventListener("click", function () {
    if (tagsDropdown.classList.contains("hidden")) {
    tagsDropdown.classList.remove("hidden");
    loadTags(); // Загрузка тегов при открытии
} else {
    tagsDropdown.classList.add("hidden");
}
});

    // Закрытие списка при клике вне его области
    document.addEventListener("click", function (event) {
    if (!tagsInput.contains(event.target) && !tagsDropdown.contains(event.target)) {
    tagsDropdown.classList.add("hidden");
}
});
});