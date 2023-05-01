import styled from 'styled-components';
import { INoAccountProps } from './IRank';

const Wrapper = styled.div`
  background-color: ${(props) => props.theme.secondary};
  border-radius: 8px;
  height: 56px;
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 0px 16px;
  color: ${(props) => props.theme.fontBlack};
  font-size: 14px;
`;

const NoAccount = (props: INoAccountProps) => {
  return (
    <Wrapper onClick={props.onClick}>
      {props.curRank == 0 ? (
        <>로그인 하러 가기</>
      ) : props.curRank == 1 ? (
        <>백준 계정 등록 하러 가기</>
      ) : (
        <>해당 유저가 존재하지 않습니다.</>
      )}
    </Wrapper>
  );
};

export default NoAccount;
