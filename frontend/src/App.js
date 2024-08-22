import React from 'react';
import { BrowserRouter as Router, Route, Routes } from 'react-router-dom';
import Index from './index0';
import LoginPage from './pages/LoginPage';
import Register from './pages/Register';
import ArticlesPage from './pages/ArticlesPage';
import Test1 from './Test1';
import UserData from './pages/UserData';

import './styles/App.css';
// import Header from './components/Header';
// import Footer from './components/Footer';

const App = () => {
  return (
    <Router>
    {/* <Header /> */}
    <main>
      <Routes>
        <Route path="/" element={<Index />} />
        <Route path="/login" element={<LoginPage />} />
        <Route path="/register" element={<Register />} />
        <Route path="/test1" element={<Test1 />} />
        <Route path="/articlesPage" element={<ArticlesPage />} />
        <Route path="/UserData" element={<UserData />} />
        {/* 你可以在这里添加更多的路由 */}
      </Routes>
    </main>
    {/* <Footer /> */}
  </Router>
  );
};

export default App;
