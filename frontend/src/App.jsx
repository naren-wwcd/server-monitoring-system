import { BrowserRouter, Routes, Route } from 'react-router-dom';
import Dashboard from './Dashboard';
import ServerDetail from './ServerDetail';

function App() {
  return (
    <BrowserRouter>
      <Routes>
        <Route path="/" element={<Dashboard />} />
        <Route path="/servers/:id" element={<ServerDetail />} />
      </Routes>
    </BrowserRouter>
  );
}

export default App;