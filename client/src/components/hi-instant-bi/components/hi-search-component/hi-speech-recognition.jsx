import React, { useEffect } from "react";
import { useDispatch } from "react-redux";
import SpeechRecognition, {
  useSpeechRecognition,
} from "react-speech-recognition";
import { updateSearchValue } from "../../../../redux/actions/instant-bi.actions";
import { AudioOutlined, AudioMutedOutlined } from "@ant-design/icons";
import { Button } from "antd";

const Dictaphone = () => {
  const {
    transcript,
    listening,
    resetTranscript,
    browserSupportsSpeechRecognition,
    isMicrophoneAvailable,
  } = useSpeechRecognition();
  const dispatch = useDispatch();

  // functions and useEffects

  useEffect(() => {
    // if (!listening) {
    if (transcript) {
      dispatch(updateSearchValue(transcript.toString()));
    }
    // }
  }, [transcript]);

  if (!isMicrophoneAvailable) {
    // Render some fallback content
  }

  // rendering

  if (!browserSupportsSpeechRecognition) {
    return <span>Browser doesn't support speech recognition.</span>;
  }

  return (
    <div>
      {/* <p>Microphone: {listening ? "on" : "off"}</p> */}
      {!listening ? (
        <Button
          size="small"
          icon={<AudioMutedOutlined />}
          onClick={() => {
            SpeechRecognition.startListening({ continuous: true });
          }}
          className="hi-audio-icon"
        />
      ) : (
        <Button
          size="small"
          icon={<AudioOutlined />}
          onClick={() => {
            SpeechRecognition.stopListening();
            resetTranscript();
          }}
          className="hi-audio-icon"
        />
      )}
      {/* <p>{transcript}</p> */}
    </div>
  );
};
export default Dictaphone;
