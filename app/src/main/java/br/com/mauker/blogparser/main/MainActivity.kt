package br.com.mauker.blogparser.main

import android.os.Bundle
import com.google.android.material.floatingactionbutton.FloatingActionButton
import androidx.appcompat.app.AppCompatActivity
import android.widget.ProgressBar
import android.widget.TextView
import br.com.mauker.blogparser.R
import br.com.mauker.blogparser.extensionFunctions.gone
import br.com.mauker.blogparser.extensionFunctions.visible
import org.koin.android.ext.android.inject
import org.koin.core.parameter.parametersOf

class MainActivity : AppCompatActivity(), MainContract.View {

    override val presenter: MainContract.Presenter by inject { parametersOf(this) }

    //region Views
    private lateinit var txtRequest1: TextView
    private lateinit var txtRequest2: TextView
    private lateinit var txtRequest3: TextView
    private lateinit var loading1: ProgressBar
    private lateinit var loading2: ProgressBar
    private lateinit var loading3: ProgressBar
    private lateinit var fab: FloatingActionButton
    //endregion

    //region Lifecycle methods
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(findViewById(R.id.toolbar))

        bindViews()
        setupButton()

        presenter.start()
    }

    override fun onStop() {
        super.onStop()
        presenter.stop()
    }

    //endregion

    //region Util methods

    private fun bindViews() {
        txtRequest1 = findViewById(R.id.txtRequest1Content)
        txtRequest2 = findViewById(R.id.txtRequest2Content)
        txtRequest3 = findViewById(R.id.txtRequest3Content)
        loading1 = findViewById(R.id.pbRequest1)
        loading2 = findViewById(R.id.pbRequest2)
        loading3 = findViewById(R.id.pbRequest3)
        fab = findViewById(R.id.fab)
    }

    private fun setupButton() {
        fab.setOnClickListener {
            presenter.onSyncClicked()
        }
    }

    //endregion

    //region View methods
    override fun setupInitialTexts() {
        val defaultText = getString(R.string.act_main_default_string)

        txtRequest1.text = defaultText
        txtRequest2.text = defaultText
        txtRequest3.text = defaultText
    }

    override fun showNoConnectionTexts() {
        val errorText = getString(R.string.act_main_no_connection_error)

        txtRequest1.text = errorText
        txtRequest2.text = errorText
        txtRequest3.text = errorText
    }

    override fun enableSyncButton() {
        fab.isEnabled = true
    }

    override fun disableSyncButton() {
        fab.isEnabled = false
    }

    override fun setRequest10thCharText(text: String) {
        txtRequest1.text = text
    }

    override fun setRequestEvery10thCharText(text: String) {
        txtRequest2.text = text
    }

    override fun setRequestCountWordText(text: String) {
        txtRequest3.text = text
    }

    override fun showRequest10thCharLoading() {
        txtRequest1.gone()
        loading1.visible()
    }

    override fun hideRequest10thCharLoading() {
        txtRequest1.visible()
        loading1.gone()
    }

    override fun showRequestEvery10thCharLoading() {
        txtRequest2.gone()
        loading2.visible()
    }

    override fun hideRequestEvery10thCharLoading() {
        txtRequest2.visible()
        loading2.gone()
    }

    override fun showRequestWordCountLoading() {
        txtRequest3.gone()
        loading3.visible()
    }

    override fun hideRequestWordCountLoading() {
        txtRequest3.visible()
        loading3.gone()
    }
    //endregion
}