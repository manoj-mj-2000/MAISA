import React, {useState} from "react";
import { useRef } from "react";
import axios from 'axios';
import ReactMarkdown from 'react-markdown'
import remarkGfm from 'remark-gfm' // GitHub-flavored markdown (tables, task lists, etc.)
import rehypeHighlight from 'rehype-highlight' // syntax highlight for code blocks
import 'highlight.js/styles/github.css'; // Optional: highlight.js theme

function AskFromPdf(){
    const [loading, setLoading] = useState(false);
    var [question, setQuestion] = useState('');
    var [url, setUrl] = useState('');
    const [answer, setAnswer] = useState('');
    const [pdfFile, setPdfFile] = useState(null);
    const fileInputRef = useRef(null);

    const handleChange = (value) => {

        const urlRegex = /(https?:\/\/[^\s]+)/g;
        const matchedUrls = value.match(urlRegex);

        if(matchedUrls){
            url = matchedUrls[0];
            const qus = value.replace(urlRegex, '').trim();
            
            question = qus == '' ? "Give a summary of this" : qus;
        }
    }

    const handleSubmit = async (e) => {
        e.preventDefault();
        setAnswer('');
        handleChange(question);

        if((!url || url == '') && (!pdfFile)){
            alert("Please attach a file or give a URL");
            setQuestion('');
            return;
        }

        var api_url = "http://localhost:8080/api/gemini/ask-from-url";
        const formData = new FormData();
        if(url){
            formData.append('url', url);
        }
        if(pdfFile){
            formData.append('file', pdfFile);
            api_url = "http://localhost:8080/api/gemini/ask-from-pdf";
        }
        formData.append('question', question);

        try{
            setLoading(true);
            const res = await axios.post(api_url, formData, {
                headers: {
                    'Content-Type': url ? 'application/json' : 'multipart/form-data'
                }
            });
            setAnswer(res.data.answer);
        }
        catch(error){
            console.log(error);
            setAnswer('Something went wrong');
        }
        finally{
            setLoading(false);
            setQuestion('');
            setUrl('');
            setPdfFile(null);
            fileInputRef.current.value = null;
        }
    };

    return (

        <div className="max-w-4xl mx-auto mt-10 p-4 border rounded shadow">

            <h2 className="text-xl font-semibold mb-4 text-center">MAISA - Search Assistant for PDFs & Website</h2>
            <form onSubmit={handleSubmit} className="flex flex-col gap-4">
                <input type='file' accept="application/pdf" onChange={(e)=> setPdfFile(e.target.files[0])} ref={fileInputRef}/>
                <input type='text' placeholder="What's on your mind?" value={question} onChange={(e)=> setQuestion(e.target.value)} className="border p-2 rounded"/>
                <button type="submit" className="bg-blue-500 text-white px-4 py-2 rounded hover:bg-blue-600">
                    {loading ? (
                        <div className="flex items-center justify-center gap-2">
                        <span className="loader"></span>
                        <span>Processing...</span>
                        </div>
                    ) : (
                        "Ask"
                    )}
                </button>
            </form>

            {answer && (
                    <div className="mt-6 p-4 bg-gray-100 border rounded prose max-w-full">
                        <h3 className="font-semibold">Answer:</h3>
                        <ReactMarkdown
                            children={answer}
                            remarkPlugins={[remarkGfm]}
                            rehypePlugins={[rehypeHighlight]}
                        />
                    </div>
                )
            }

        </div>

    );

}
export default AskFromPdf;