import {useContext} from 'react';
import {DictionaryCtx} from '@/context/DictionaryCtx';

export default function DictionaryList() {
    const {words} = useContext(DictionaryCtx);
    return (
        <div className="p-6 grid gap-3">
            {words.map(w => (
                <div key={w.lemma} className="p-4 rounded-2xl shadow bg-white">
                    <h2 className="text-xl font-semibold">{w.lemma}</h2>
                    <p className="text-sm text-gray-600 italic">{w.ipa}</p>
                    <p>{w.meaning}</p>
                </div>
            ))}
        </div>
    );
}
