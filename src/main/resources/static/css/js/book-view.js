const button = document.getElementById("delete-button");

button.addEventListener("click", async () => {
    const id = button.dataset.bookId;

    if (!confirm("Delete this book?")) {
        return;
    }

    const response = await fetch(`/api/books/${id}`, {
        method: "DELETE"
    });

    if (response.ok) {
        window.location.href = "/books";
    } else {
        alert("Failed to delete book");
    }
});