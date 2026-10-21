import { Button, Modal, Progress } from "antd";
import { transferPhaseMessage } from "../../base/resumable-transfer";

const DownloadProgressModal = ({
  downloadingInfo,
  onCancel,
  className = "hi-hr-download-modal",
}) => {
  const phase = downloadingInfo?.phase;
  // Only resumable transfers attach real pause/resume handlers.
  const canPauseResume =
    typeof downloadingInfo?.pause === "function" &&
    typeof downloadingInfo?.resume === "function";
  const pause = () => {
    downloadingInfo.pause();
  };
  const resume = () => {
    downloadingInfo.resume();
  };

  return (
    <Modal
      visible={!!downloadingInfo}
      className={className}
      title="Downloading"
      okText={() => null}
      cancelText="Cancel"
      maskClosable={false}
      onCancel={onCancel}
      footer={[
        canPauseResume && (phase === "downloading" || phase === "reconnecting") ? (
          <Button key="pause" onClick={pause}>
            Pause
          </Button>
        ) : null,
        canPauseResume && phase === "paused" ? (
          <Button key="resume" onClick={resume}>
            Resume
          </Button>
        ) : null,
        <Button key="cancel" onClick={onCancel}>
          Cancel
        </Button>,
      ].filter(Boolean)}
    >
      <div>
        <Progress percent={downloadingInfo?.progress ?? 0} />
        <div>{transferPhaseMessage(phase, "download")}</div>
      </div>
    </Modal>
  );
};

export default DownloadProgressModal;
