async function submitBook(event) {
    event.preventDefault();

    const bookId = document.getElementById('bookId').value;
    const title = document.getElementById('title').value.trim();
    const authorId = document.getElementById('authorId').value;
    const genreId = document.getElementById('genreId').value;

    if (!title) {
        document.getElementById('error').textContent = 'Title must not be empty';
        return;
    }

    const body = JSON.stringify({
        title,
        authorId,
        genreId
    });

    const isNew = !bookId;

    const res = await fetch(
        isNew ? '/api/books' : '/api/books/' + bookId,
        {
            method: isNew ? 'POST' : 'PUT',
            headers: {
                'Content-Type': 'application/json'
            },
            body
        }
    );

    if (res.ok) {
        window.location.href = '/books';
    } else {
        const data = await res.json().catch(() => ({}));
        document.getElementById('error').textContent =
            data.error || 'Error saving book';
    }
}

document.addEventListener('DOMContentLoaded', () => {
    document.getElementById('book-form')
        .addEventListener('submit', submitBook);
});