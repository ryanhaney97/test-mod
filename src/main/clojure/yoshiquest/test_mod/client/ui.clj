(ns yoshiquest.test-mod.client.ui
  (:require
   [forge-clj.client.ui :refer [defguicontainer]]
   [forge-clj.tileentity :refer [get-tile-entity-at]]
   [forge-clj.util :refer [construct]]
   [forge-clj.client.util :refer [bind-texture set-gl-color]]
   [yoshiquest.test-mod.ui :refer [get-client-gui get-server-gui]]))

(defguicontainer yoshiquest.test-mod.client.ui test-gui-container
  :expose-fields {guiLeft {:get getGuiLeft}
                  guiTop {:get getGuiTop}
                  xSize {:get getXSize}
                  ySize {:get getYSize}
                  fontRendererObj {:get getFontRenderer}})

(defn test-gui-container-drawGuiContainerBackgroundLayer [^TestGuiContainer this ticks mouse-x mouse-y]
  (set-gl-color 1 1 1 1)
  (bind-texture "test-mod:textures/gui/test-gui.png")
  (.drawTexturedModalRect this (.getGuiLeft this) (.getGuiTop this) 0 0 (.getXSize this) (.getYSize this)))

(defn test-gui-container-drawGuiContainerForegroundLayer [^TestGuiContainer this mouse-x mouse-y]
  (let [display-name "Test Inventory"]
    (.drawString (.getFontRenderer this) display-name (- 88 (/ (.getStringWidth (.getFontRenderer this) display-name) 2)) 6 (int 0x404040))))

(defn client-gui-fn [id player world x y z]
  (condp = id
    0 (construct test-gui-container (get-server-gui 0 player world x y z))
    nil))

(defn init-client-gui []
  (reset! get-client-gui client-gui-fn))
