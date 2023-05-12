import { useState } from 'react';
import { InputDiv } from './NewCareer';
import DatePicker from './DatePicker';
import styled from 'styled-components';
import CancelOk from '../common/CancelOk';
import { IDdayModalProps } from './ICareer';
import { DarkBg } from './SCareer';

const ModalDiv = styled.div`
  position: fixed;
  left: 0;
  z-index: 6;
  .modalContent {
    position: fixed;
    bottom: 0;
    width: 100vw;
    z-index: 15;
    background-color: white;
  }
  .modalTitle {
    margin: 1rem;
    border-bottom: 2px solid ${(props) => props.theme.primary};
    text-align: center;
    font-size: large;
  }
`;

const DdayModal = ({ close, result }: IDdayModalProps) => {
  const [ddayName, setDdayName] = useState<string>('');
  const [date, setDate] = useState<string>('');
  const updateDate = (date: string) => {
    setDate(date);
  };

  const ok = () => {
    result({ ddayName, date });
    close();
  };

  return (
    <ModalDiv>
      <DarkBg onClick={close}></DarkBg>
      <div className="modalContent">
        <div className="modalTitle">다음일정 변경하기</div>
        <InputDiv>
          <div>다음일정</div>
          <input
            type="text"
            className="input"
            placeholder="ex) 코딩테스트, 1차 면접"
            value={ddayName}
            onChange={(e: React.ChangeEvent<HTMLInputElement>) => setDdayName(e.target.value)}
          ></input>
        </InputDiv>
        <DatePicker updateDate={updateDate}></DatePicker>
        <CancelOk cancel={close} ok={ok}></CancelOk>
      </div>
    </ModalDiv>
  );
};

export default DdayModal;
