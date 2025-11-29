const {createApp, ref} = Vue;

createApp({
    setup() {
        const form = ref({login: '', password: ''});
        const message = ref(null);

        const login = async () => {
            message.value = null;
            const res = await fetch('/api/auth/login', {
                method: 'POST',
                headers: {'Content-Type': 'application/json'},
                body: JSON.stringify(form.value)
            });
            const data = await res.json();
            if (res.ok) {
                message.value = {type: 'success', text: data.message};
                setTimeout(() => window.location.href = 'main.html', 1000);
            } else {
                message.value = {type: 'error', text: data.error || 'Ошибка входа'};
            }
        };
        return {form, message, login};
    }
}).mount('#app');