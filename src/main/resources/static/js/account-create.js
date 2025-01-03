document.getElementById('accountForm').addEventListener('submit', function(e) {
    e.preventDefault();
    
    const password = document.getElementById('password').value;
    const confirmPassword = document.getElementById('confirm-password').value;
    
    if (password !== confirmPassword) {
        alert('パスワードが一致しません。');
        return;
    }
    
    const formData = {
        name: document.getElementById('name').value,
        email: document.getElementById('email').value,
        password: password
    };

    const token = document.querySelector('meta[name="_csrf"]').getAttribute('content');
    const header = document.querySelector('meta[name="_csrf_header"]').getAttribute('content');
    const headers = {
        'Content-Type': 'application/json'
    };
    headers[header] = token;
    
    fetch('/api/account/create', {
        method: 'POST',
        headers: headers,
        body: JSON.stringify(formData)
    })
    .then(response => {
        if (!response.ok) {
            return response.json().then(data => {
                throw new Error(data.message || 'アカウント作成に失敗しました。');
            });
        }
        return response.json();
    })
    .then(() => {
        window.location.href = '/login';
    })
    .catch(error => {
        alert(error.message);
    });
}); 