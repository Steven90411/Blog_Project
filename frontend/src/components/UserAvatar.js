import React, { useState, useEffect } from 'react';
import styles from "../styles/components/UserAvatar.module.css";
import Modal from 'react-modal';
import ImageUpload from "../components/ImageUpload";

const UserAvatar = ({ id }) => {
    // const [isModalOpen, setIsModalOpen] = useState(false);// 設定彈跳視窗開關


    // 用來管理用戶資料
    const [userData, setUserData] = useState({
        imagelink:''
    });
    const [loading, setLoading] = useState(false);
    const [error, setError] = useState('');

    // 透過圖片路徑顯示圖片
    // 獲取後端資料
    useEffect(() => {
        setLoading(true);

        fetch(`http://localhost:8080/blog/api/userProfile/${id}`)
            .then(response => {
                console.log('網頁回應:', response);
                return response.json();
            })
            .then(data => {
                console.log("得到的數據", data)
                setUserData(data);
                setLoading(false);
            })
            .catch(error => {
                console.error("獲取用戶資料失敗", error);
                setError("獲取用戶資料失敗");
                setLoading(false);
            })
    }, [id]);

    const [isModalOpen, setIsModalOpen] = useState(false);


    const openModal = () => {
        setIsModalOpen(true);
    };

    const customStyles = {
        content: {
            width: '70%', 
            height: '70%', 
            top: '50%',
            left: '50%',
            right: 'auto',
            bottom: 'auto',
            marginRight: '-50%',
            transform: 'translate(-50%, -50%)'
        }
    };
    

    return (
        <div className={`${styles.profile_picture_wrapper} text-center`}>
            {loading && <p>載入中...</p>}
            {error && <p className={styles.error}>{error}</p>}
            <label className={`${styles.avatarName} form_label d_block`}>我的頭像</label>
            <div className="image-container mb-3">
                <img
                    id={styles.profile_avatar}
                    src={userData.imagelink}
                    alt="頭像"
                    className="img-fluid rounded border border-3 border-dark"
                />
            </div>
            <button
                type="button"
                className={`btn btn-dark ${styles.photo}`}
            onClick={() => setIsModalOpen(true)}
            >
                更新頭像
            </button>

            <Modal
                isOpen={isModalOpen}
                onRequestClose={() => setIsModalOpen(false)}
                contentLabel="Crop Avatar"
                style={customStyles}
                ariaHideApp={false}
            >
                <ImageUpload id={id}/>
            </Modal>
        </div>
    );
};

export default UserAvatar;