import React from 'react';
import ReactDOM from 'react-dom/client';
import {BrowserRouter, Route, Routes} from 'react-router-dom';
import './index.css';
import DictionaryList from '@/pages/DictionaryList';
import AdminEditor from '@/pages/AdminEditor';
import {DictionaryProvider} from '@/context/DictionaryCtx';

ReactDOM.createRoot(document.getElementById('root')!).render(
    <React.StrictMode>
        <DictionaryProvider>
            <BrowserRouter>
                <Routes>
                    <Route path="/" element={<DictionaryList/>}/>
                    <Route path="/admin" element={<AdminEditor/>}/>
                </Routes>
            </BrowserRouter>
        </DictionaryProvider>
    </React.StrictMode>,
);
