import { addWord } from '@/api/client';

const handleSubmit = async () => {
    await addWord(form);
    setForm(init);
};