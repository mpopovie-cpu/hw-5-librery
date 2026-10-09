async function loadBooks() {
    const response = await fetch("/api/books");
    const books = await response.json();

    const tbody = document.getElementById("books-body");
    tbody.innerHTML = "";

    books.forEach(book => {
        const row = document.createElement("tr");

        row.innerHTML = `
            <td>${book.title}</td>
            <td>${book.authorName || ""}</td>
            <td>${book.genreName || ""}</td>
            <td>
                <a href="/books/${book.id}">View</a>
                <a href="/books/${book.id}/edit">Edit</a>
                <button type="button" data-id="${book.id}" class="delete-button">
                    Delete
                </button>
            </td>
        `;

        tbody.appendChild(row);
    });

    document.querySelectorAll(".delete-button").forEach(button => {
        button.addEventListener("click", async () => {
            if (!confirm("Delete this book?")) {
                return;
            }

            const response = await fetch(`/api/books/${button.dataset.id}`, {
                method: "DELETE"
            });

            if (response.ok) {
                loadBooks();
            } else {
                alert("Failed to delete book");
            }
        });
    });
}

loadBooks();