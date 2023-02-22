import java.awt.BorderLayout
import java.awt.Color
import java.awt.Dimension
import java.awt.GridLayout
import java.awt.event.ActionEvent
import java.awt.event.ItemEvent
import java.io.PrintWriter
import java.util.*
import javax.swing.*

class GUI : JFrame() {
    private var verticalTree: VerticalTree<UserType>
    private var sample: UserType
    private val treeArea: JTextArea
    private val outputArea: JTextArea
    var typeComboBox: JComboBox<String>
    private val valueTextField: JTextField
    private val indexTextField: JTextField
    private val addButton: JButton
    private val getButton: JButton
    private val removeButton: JButton
    private val balanceButton: JButton
    private val savebutton: JButton
    private val loadButton: JButton
    private fun updateTreeView() {
        treeArea.text = verticalTree.toString()
    }

    private fun addMessage(message: String) {
        outputArea.text = outputArea.text + message + System.lineSeparator()
    }

    private fun addErrorMessage(message: String) {
        outputArea.text = outputArea.text + "ERROR!: " + message + System.lineSeparator()
    }

    companion object {
        @JvmStatic
        fun main(args: Array<String>) {
            val userFactory = UserFactory()
            SwingUtilities.invokeLater { GUI() }
        }
    }

    init {
        title = "Vertical tree"
        preferredSize = Dimension(600, 600)
        isResizable = false
        contentPane.layout = BorderLayout()
        treeArea = JTextArea()
        treeArea.isEditable = false
        treeArea.border = BorderFactory.createLineBorder(Color.black)
        val treeScrollPane = JScrollPane(treeArea)
        treeScrollPane.horizontalScrollBarPolicy = ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED
        treeScrollPane.verticalScrollBarPolicy = ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS
        contentPane.add(treeScrollPane, BorderLayout.CENTER)
        outputArea = JTextArea()
        outputArea.isEditable = false
        outputArea.rows = 5
        outputArea.border = BorderFactory.createLineBorder(Color.black)
        outputArea.lineWrap = true
        val outputScrollPane = JScrollPane(outputArea)
        outputScrollPane.horizontalScrollBarPolicy = ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER
        outputScrollPane.verticalScrollBarPolicy = ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS
        contentPane.add(outputScrollPane, BorderLayout.SOUTH)
        val controlPanel = JPanel()
        val gridLayout = GridLayout(4, 1)
        gridLayout.vgap = 30
        controlPanel.layout = gridLayout
        controlPanel.border = BorderFactory.createLineBorder(Color.black)
        contentPane.add(controlPanel, BorderLayout.EAST)
        val inputButtonPanel = JPanel(BorderLayout())
        val userFactory = UserFactory()
        typeComboBox = JComboBox<String>(DefaultComboBoxModel(userFactory.typeNameList().toTypedArray()))
        typeComboBox.selectedIndex = 0
        sample = userFactory.getBuilderByName(typeComboBox.selectedItem.toString())
        verticalTree = VerticalTree<UserType>(sample)
        typeComboBox.addItemListener { e: ItemEvent ->
            if (e.stateChange == ItemEvent.SELECTED) {
                val item = e.item.toString()
                sample = userFactory.getBuilderByName(item)
                verticalTree = VerticalTree<UserType>(sample)
                updateTreeView()
            }
        }
        controlPanel.add(typeComboBox)
        val inputPanel = JPanel(GridLayout(2, 2))
        inputPanel.add(JLabel("Value:"))
        valueTextField = JTextField()
        inputPanel.add(valueTextField)
        addButton = JButton("Add")
        inputButtonPanel.add(inputPanel, BorderLayout.CENTER)
        inputButtonPanel.add(addButton, BorderLayout.SOUTH)
        inputButtonPanel.border = BorderFactory.createLineBorder(Color.black)
        addButton.addActionListener { e: ActionEvent? ->
            val value: UserType?
            value = try {
                sample.parseValue(valueTextField.text) as UserType?
            } catch (ex: Exception) {
                addErrorMessage("Can not parse " + sample.typeName() + " from text field")
                return@addActionListener
            }
            addMessage(value.toString() + " was added to the tree")
            if (value != null) {
                verticalTree.add(value)
            }
            updateTreeView()
        }
        controlPanel.add(inputButtonPanel)
        val elementPanel = JPanel(GridLayout(3, 1))
        indexTextField = JTextField()
        indexTextField.horizontalAlignment = SwingConstants.CENTER
        elementPanel.add(indexTextField)
        getButton = JButton("Get at index")
        getButton.addActionListener { e: ActionEvent? ->
            try {
                val index = indexTextField.text.toInt()
                val t: UserType? = verticalTree.get(index)
                addMessage("$t is at index $index")
            } catch (ex: IndexOutOfBoundsException) {
                addErrorMessage("Input index is out of bounds")
            } catch (ex: Exception) {
                addErrorMessage("Can not parse index from input index field")
            }
        }
        elementPanel.add(getButton)
        removeButton = JButton("Remove at index")
        removeButton.addActionListener { e: ActionEvent? ->
            try {
                val index = indexTextField.text.toInt()
                val u: UserType? = verticalTree.remove(index)
                updateTreeView()
                addMessage("$u removed from index $index")
            } catch (ex: IndexOutOfBoundsException) {
                addErrorMessage("Input index is out of bounds")
            } catch (ex: Exception) {
                addErrorMessage("Can not parse index from input index field")
            }
        }
        elementPanel.add(removeButton)
        elementPanel.border = BorderFactory.createLineBorder(Color.black)
        controlPanel.add(elementPanel)
        val buttonPanel = JPanel(GridLayout(3, 1))
        balanceButton = JButton("Balance tree")
        buttonPanel.add(balanceButton)
        balanceButton.addActionListener { e: ActionEvent? ->
            verticalTree.balance()
            updateTreeView()
            addMessage("Tree was balanced")
        }
        buttonPanel.border = BorderFactory.createLineBorder(Color.black)
        savebutton = JButton("Save tree")
        buttonPanel.add(savebutton)
        savebutton.addActionListener { e: ActionEvent? ->
            val fileChooser = JFileChooser(".")
            fileChooser.showSaveDialog(this)
            try {
                PrintWriter(fileChooser.selectedFile).use { writer -> writer.print(verticalTree.serialize()) }
            } catch (ex: Exception) {
                addErrorMessage("Can not save to file: " + ex.message)
            }
        }
        loadButton = JButton("Load tree")
        buttonPanel.add(loadButton)
        loadButton.addActionListener { e: ActionEvent? ->
            val fileChooser = JFileChooser(".")
            fileChooser.showOpenDialog(this)
            try {
                Scanner(fileChooser.selectedFile).use { scanner ->
                    val builder = StringBuilder()
                    while (scanner.hasNextLine()) {
                        builder.append(scanner.nextLine()).append(System.lineSeparator())
                    }
                    verticalTree = VerticalTree.deserialize(builder.toString(), sample.javaClass)
                    updateTreeView()
                }
            } catch (ex: Exception) {
                addErrorMessage("Can not load from file: " + ex.message)
            }
        }
        controlPanel.add(buttonPanel)
        isVisible = true
        defaultCloseOperation = EXIT_ON_CLOSE
        pack()
    }
}
