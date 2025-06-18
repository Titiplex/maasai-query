import React, {createContext, useEffect, useState} from 'react';
import {listWords, Vocabulary} from '@/api/client';
import {connectWS, stompClient} from '@/ws';

type Ctx = { words: Vocabulary[] };
export const DictionaryCtx = createContext<Ctx>({words: []});

export const DictionaryProvider: React.FC<{ children: React.ReactNode }> = ({children}) => {
    const [words, setWords] = useState<Vocabulary[]>([]);

    useEffect(() => {
        listWords().then(setWords);
        connectWS();
        stompClient.onConnect = () => {
            stompClient.subscribe('/topic/vocabulary', (msg: { body: string; }) => {
                const v: Vocabulary = JSON.parse(msg.body);
                setWords(prev => {
                    const idx = prev.findIndex(p => p.lemma === v.lemma);
                    return idx >= 0 ? [...prev.slice(0, idx), v, ...prev.slice(idx + 1)] : [v, ...prev];
                });
            });
        };
    }, []);

    return <DictionaryCtx.Provider value={{words}}>{children}</DictionaryCtx.Provider>;
};