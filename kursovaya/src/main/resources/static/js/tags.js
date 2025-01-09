
    document.addEventListener("DOMContentLoaded", function () {
    const tagsInput = document.getElementById("tags-input");
    const tagsDropdown = document.getElementById("tags-dropdown");


    function loadTags() {
    fetch('/tags')
    .then(response => response.json())
    .then(tags => {
    tagsDropdown.innerHTML = '';
    tags.forEach(tag => {
    const tagItem = document.createElement("div");
    tagItem.textContent = tag.name;
    tagItem.dataset.tagName = tag.name;
    tagsDropdown.appendChild(tagItem);

    tagItem.addEventListener("click", function () {
    tagsInput.value = this.dataset.tagName;
    tagsDropdown.classList.add("hidden");
});
});
})
    .catch(error => console.error("Ошибка при загрузке тегов:", error));
}

    tagsInput.addEventListener("click", function () {
    if (tagsDropdown.classList.contains("hidden")) {
    tagsDropdown.classList.remove("hidden");
    loadTags();
} else {
    tagsDropdown.classList.add("hidden");
}
});

    document.addEventListener("click", function (event) {
    if (!tagsInput.contains(event.target) && !tagsDropdown.contains(event.target)) {
    tagsDropdown.classList.add("hidden");
}
});
});