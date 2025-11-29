const {createApp, ref} = Vue;

createApp({
    setup() {
        const form = ref({login: '', password: '', confirm: ''});
        const message = ref(null);

        const register = async () => {
            message.value = null;
            if (form.value.password !== form.value.confirm) {
                message.value = {type: 'error', text: 'Пароли не совпадают'};
                return;
            }
            const response = await fetch('/api/auth/register', {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json',
                    'Accept': 'application/json'
                },
                body: JSON.stringify({
                    login: form.value.login.trim(),
                    password: form.value.password
                })
            });
            const data = await response.json();
            if (response.ok) {
                message.value = {type: 'success', text: data.message || 'Регистрация успешна!'};
                setTimeout(() => {window.location.href = 'login.html';}, 1000);
            } else {
                message.value = {type: 'error', text: data.error || `Ошибка регистрации (${response.status})`};
            }
        };
        return {form, message, register};
    }
}).mount('#app');