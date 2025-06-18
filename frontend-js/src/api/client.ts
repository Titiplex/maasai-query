import axios from 'axios';

export const api = axios.create({
    baseURL: import.meta.env.VITE_API_URL ?? 'http://localhost:8080/api/v1',
});
api.interceptors.request.use(cfg => {
    const t = localStorage.getItem('jwt');
    if (t) cfg.headers.Authorization = `Bearer ${t}`;
    return cfg;
});

export type Vocabulary = { lemma: string; ipa?: string; meaning: string };
export const listWords = () => api.get<Vocabulary[]>('/vocabulary').then(r => r.data);
export const addWord = (w: Vocabulary) => api.post('/vocabulary', w);